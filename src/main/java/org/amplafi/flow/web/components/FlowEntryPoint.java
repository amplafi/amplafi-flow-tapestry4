/*
 * Created on May 9, 2005
 *
 */
package org.amplafi.flow.web.components;

import static org.apache.commons.lang.StringUtils.*;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.amplafi.flow.FlowConstants;
import org.amplafi.flow.FlowState;
import org.amplafi.flow.FlowUtils;
import org.amplafi.flow.launcher.FlowLauncher;
import org.amplafi.flow.launcher.StartFromDefinitionFlowLauncher;
import org.amplafi.flow.validation.FlowValidationException;
import org.amplafi.flow.web.BaseFlowComponent;
import org.amplafi.flow.web.FlowResultHandler;
import org.amplafi.flow.web.FlowWebUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.link.DirectLink;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.form.Form;
import org.apache.tapestry.form.Submit;
import org.apache.tapestry.form.LinkSubmit;
import net.sf.tacos.annotations.Cached;
import net.sf.tacos.annotations.InjectParameterFlag;


/**
 * This represents the link to a new flow or continuing an existing flow. This
 * class is responsible for the creation of a new FlowState or the activation of
 * an existing flowstate. Control is then transfered to the flow's current
 * activity.
 * If the point is shown, the body of the component will also be rendered (first).
 *
 * See the comment on {@link #getFlowTypeName()} to see how the flow to launch is determined.
 *
 * TODO should preserve the current flow (if the current flow is not be finished),
 * so that the flows can be "stacked". This will help with determining which flows are still active
 * and will allow "call/return" subflow construction.
 */
@ComponentClass(allowBody=true, allowInformalParameters=true)
public abstract class FlowEntryPoint extends BaseFlowComponent {
    /**
     *
     */
    private static final String FLOW_ENTRY_POINT = "FlowEntryPoint";

    /**
     * This value will not be available when {@link #doEnterFlow(FlowLauncher, String, Iterable)} is called,
     * so it will be passed explicitly.
     * @return a previously created FlowLauncher
     */
    @Parameter
    public abstract FlowLauncher getFlowLauncher();
    /**
     * Used when the FlowEntryPoint is to launch a new flow.
     * FlowTypeName can also be specified by setting the jwcid of the FlowEntryPoint component.
     * For example, 'FooBar@flow:FlowEntryPoint' or 'fooBar@flow:FlowEntryPoint' both indicate that
     * the 'FooBar' flow should be started.
     *
     * If multiple FlowEntryPoint can start the same flow then each entrypoint can be named like this:
     * 'fooBarFlowEntryPoint1@flow:FlowEntryPoint', 'fooBarFlowEntryPoint2@flow:FlowEntryPoint' ...
     * @return flowType to launch
     */
    @Parameter
    public abstract String getFlowTypeName();

    /**
     * Used to initialize the flow. Each string is of the form "flowKey1=flowValue1"
     * If 'flowValue' is a FormComponent it usually has a 'value' parameter. That value parameter
     * is used as the value to be assigned to the flow's flowKey1's initial value.
     * @return assigned initial values.
     */
    @Parameter
    public abstract Object getInitialValues();
    @Parameter(defaultValue="true") // for now...
    public abstract boolean isFormValuesNotUsed();

    @Parameter(name="class")
    public abstract String getHtmlClass();

    /**
     * @return
     * Controls the existence of listener on submit buttons.
     */
    @Parameter
    public abstract boolean getRemoveListeners();

    /**
     * Using this allows a flow to be finished
     * and another started with one button click.
     * @return the id of the flow to finish when this flow is entered.
     */
    @Parameter
    public abstract String getFinishFlowId();

    /**
     * finish the {@link #getAttachedFlowState()}.
     * @return true the attached flow should be finished.
     */
    @Parameter
    public abstract Boolean getFinishCurrentFlow();

    /**
     * title to override the default flow title supplied by flow definition.
     * @return the link for the flow.
     */
    @Parameter
    public abstract String getTitle();

    /**
     * &lt;input type="submit" value="the label"/&gt;
     * @return another way to specify the label.
     */
    @Parameter
    public abstract String getValue();

    /**
     * If this is set then the FlowEntryPoint is being rendered as a button.
     * &lt;input type="submit" ...&gt;
     *
     * or
     * &lt;input type="cancel" ...&gt;
     * @return the type value if the user is using &lt;input/&gt;
     */
    @Parameter(defaultValue="'submit'")
    public abstract String getType();

    @Parameter
    public abstract String getPageName();

    /**
     *
     * @return true if the container is forcing the FlowEntryPoint to be stateful.
     * False if the container is forcing the FlowEntryPoint to be stateless.
     * unbound if the container is letting the FlowEntryPoint make the decision.
     */
    @Override
    @Parameter
    public abstract boolean isStateful();
    /**
     * used by {@link FlowEntryPoint} to report what it thinks the value should be.
     * @param stateful
     */
    public abstract void setStateful(boolean stateful);
    @InjectParameterFlag
    public abstract boolean isStatefulBound();

    /**
     *
     * @return true if the flow started / continued by this FlowEntryPoint should return to the currently active
     * flow when it completes.
     */
    @Parameter(defaultValue="true")
    public abstract boolean isReturnToCurrentFlow();

    /**
     * TODO rationalize this!
     * @return
     * Always show this entry point - even if it's the same as the active one.
     */
    @Parameter
    public abstract boolean isAlwaysShow();

    /**
     *
     * @return true if any text that is encapsulated in the flowEntryPoint should be rendered. Since usually this is just
     * text for layout purposes the default is false.
     */
    @Parameter
    public abstract boolean isRenderBody();

    /**
     * Used when the container wants to make the show/no show decision.
     * @return true if the container is forcing this entrypoint to be visible,
     * false if forcing it to be hidden,
     * null if container is leaving decision to the FlowEntryPoint.
     */
    @Parameter
    public abstract Boolean getShow();

    public abstract HttpServletResponse getHttpServletResponse();

    @InjectObject("service:tapestry.services.Page")
    public abstract IEngineService getPageService();

    @Component(inheritInformalParameters=true)
    public abstract Form getInnerForm();

    @Component(id = "fepLink", inheritedBindings = {"async", "updateComponents"})
    public abstract DirectLink getDirectLink();

    @Component(id = "fepSubm", inheritedBindings = {"async", "updateComponents"})
    public abstract Submit getSubmit();

    @Component(id = "fepLiSubm", inheritedBindings = {"async", "updateComponents"})
    public abstract LinkSubmit getLinkSubmit();


    /**
     *
     * @return true if the template tag used is a &lt;input&gt;
     */
    public boolean isRenderAsButton() {
        return "input".equalsIgnoreCase(getTemplateTagName());
    }
    public boolean getStatefulSetting() {
        if ( isStatefulBound() ) {
            return isStateful();
        } else {
            // for now by default -- always stateless -- in future may change if we are continuing
            // an unpersisted flow.
            return false;
        }
    }
    @Cached
    public String getActualFlowTypeName() {
        String flowTypeName = getFlowTypeName();
        if (isBlank(flowTypeName)) {
            String id = getId();
            int index = id.indexOf(FLOW_ENTRY_POINT);
            if (index > 0 ) {
                flowTypeName = id.substring(0, index);
            } else if ( (index = id.indexOf("EntryPoint")) > 0) {
                flowTypeName = id.substring(0, index);
            } else {
                flowTypeName = id;
            }
            flowTypeName = capitalize(flowTypeName);
        }
        return flowTypeName;
    }

    public boolean isBodyPartOfLink() {
        return "a".equals(getTemplateTagName()) && !isRenderAsButton() && isRenderBody();
    }
    public abstract IRequestCycle getRequestCycle();

    public abstract FlowResultHandler getFlowResultHandler();

    // HACK ... we really should not be going to an array of strings ( if possible)
    // but rather a map. The problem with a map is that tapestry @Component bindings can easily handle lists but
    // can not easily specify maps.
    @Cached(resetAfterRewind=true)
    @SuppressWarnings("unchecked")
    protected Iterable<String> getValues() {
        ArrayList<String> values = new ArrayList<String>();
//        boolean b = /* !isFormValuesNotUsed() &&*/ isInsideForm();
//        if (isFlowEntryPointIsForm() || b) {
//            // WRONG ... ANDY how to get access to the body components?
//            Form form =(Form) this.getForm();
//            Map<Object, IComponent> formComponents = form.getComponents();
//            for(IComponent formComponent: formComponents.values()) {
//                if ( formComponent != this && formComponent instanceof IFormComponent) {
//                    IFormComponent iformComponent = (IFormComponent)formComponent;
//                    String displayName = iformComponent.getName();
//                    if ( isNotBlank(displayName)) {
//                        IBinding binding = iformComponent.getBinding("value");
//                        if ( binding != null) {
//                            values.add(displayName+"="+displayName);
//                        }
//                    }
//                }
//            }
//        }
        Object initialValues = getInitialValues();
        if (initialValues != null ) {
            if (initialValues instanceof Iterable) {
                for(String s: (Iterable<String>)initialValues) {
                    values.add(s);
                }
            } else if (initialValues instanceof Map) {
                for(Map.Entry<Object, Object> entry: ((Map<Object, Object>)initialValues).entrySet()) {
                    String key = ObjectUtils.toString(entry.getKey(), null);
                    String value = ObjectUtils.toString(entry.getValue(), null);
                    FlowUtils.addInitialValues(values, key, value);
                }
            } else {
                values.add(initialValues.toString());
            }
        }
        //  so the currently active flow can be returned to after the newly started flow completes.
        FlowState flowState = getFlowManagement().getCurrentFlowState();
        if ( isReturnToCurrentFlow() && flowState != null) {
            FlowUtils.addInitialValues(values,FlowConstants.FSRETURN_TO_FLOW, flowState.getLookupKey());
        }
        return values;
    }

    public FlowLauncher getActualFlowLauncher() {
        FlowLauncher launcher = getFlowLauncher();
        if ( launcher == null && StringUtils.isNotBlank(getActualFlowTypeName())) {
            launcher = new StartFromDefinitionFlowLauncher(getActualFlowTypeName(), getContainer(), getValues(), getFlowManagement(), null);
        }
        return launcher;
    }

    public boolean isShowEntryPoint() {
        if ( getShow() == null ) {
            return isAlwaysShow() || !isSameAsActive();
        } else {
            return getShow() != null && getShow();
        }
    }

    public String getActivePage() {
        FlowState flowState = getFlowManagement().getCurrentFlowState();
        return flowState.getCurrentPage();
    }

    /**
     * @return
     * The entry point will not be shown if the currently active flow
     * is the same type as the flow that this entry point is to launch.
     */
    @Cached
    public Boolean isSameAsActive() {
        FlowState flowState = getFlowManagement().getCurrentFlowState();
        FlowLauncher actualFlowLauncher = getActualFlowLauncher();
        if (flowState != null && actualFlowLauncher != null && flowState.getFlowTypeName().equals(actualFlowLauncher.getFlowTypeName())) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * This is an entry point to restart a previously suspended (or newly
     * created) flow from this session,
     * or create a new flow from a flow definition.
     * @param flowLauncher because on call getFlowLauncher() will not have a value.
     * @param initialValues used to define the initial values for flow. This is a
     * list of strings. Each string is 'key=value'. if value is the same name as a component
     * that has a 'value' attribute (like TextField components) then the initial value.
     * If value is a container's property then that value is used. Otherwise the value
     * provided is used as a literal.
     * @param finishFlowId if not null, the {@link FlowState} corresponding to this
     * id will be finished.
     * @return pagename of new flow's initial entry point.
     */
    public ILink doEnterFlow(FlowLauncher flowLauncher, String finishFlowId, Iterable<String> initialValues) {
        String pageName = null;
        if ( flowLauncher != null) {
            flowLauncher.setFlowManagement(getFlowManagement());
            if ( initialValues != null && flowLauncher instanceof StartFromDefinitionFlowLauncher) {
                ((StartFromDefinitionFlowLauncher)flowLauncher).setPropertyRoot(getContainer());
                ((StartFromDefinitionFlowLauncher)flowLauncher).addInitialValues(initialValues);
            }
        }
        try {
            finishFlow(finishFlowId);
            if ( flowLauncher != null ) {
                FlowState flowState = flowLauncher.call();
                pageName = (flowState != null) ? flowState.getCurrentPage() : null;
            }
        } catch (FlowValidationException e) {
            getFlowResultHandler().handleValidationTrackings(e.getTrackings(), this);
            // cleanup
            FlowState current = getFlowManagement().getCurrentFlowState();
            if (current!=null) {
                getFlowManagement().dropFlowState(current);
            }
            pageName = null;
        } finally {
            if ( pageName == null || pageName.equals(this.getPage().getPageName())) {
                this.updateComponents(findComponentsToUpdate(getUpdateComponents()));
            }
        }
        if ( isBlank(pageName)) {
            pageName = getPageName();
        }

        if ( isBlank(pageName) ) {
            return null;
        } else {
            FlowWebUtils.activatePageIfNotNull(null, pageName, null);
            return null;
        }
    }

    /**
     *
     * @param finishFlowId
     * @return the page that the completed flow was supposed to return to.
     */
    private String finishFlow(String finishFlowId) {
        if (finishFlowId != null ) {
            FlowState flowState = getFlowManagement().getFlowState(finishFlowId);
            if ( flowState != null && !flowState.isCompleted()) {
                return flowState.finishFlow();
            }
        }
        return null;
    }

    public String getFlowLabel() {
        String label = getTitle();
        if ( isBlank(label)) {
            // <input type="submit" value="label"/>
            label = getValue();
        }
        if ( isBlank(label) && getActualFlowLauncher() != null ) {
            label = getActualFlowLauncher().getFlowLabel();
        }
        if ( isBlank(label) ) {
            label = isBlank(getActualFlowTypeName())?"{no flow type}":"["+getActualFlowTypeName()+"]";
        }
        return processLabel(label, null);
    }

    public boolean isRenderedAsSubmit() {
        if ( isInsideForm() ) {
            return getInitialValues() != null || "submit".equals(getType());
        } else {
            return false;
        }
    }

    public boolean isFlowEntryPointIsForm() {
        return "form".equals(getTemplateTagName());
    }
    public String getFlowToFinish() {
        if ( getFinishFlowId() != null ) {
            return getFinishFlowId();
        } else if ( getFinishCurrentFlow() != null && getFinishCurrentFlow() && getAttachedFlowState()!=null) {
            return getAttachedFlowState().getLookupKey();
        } else {
            return null;
        }
    }
    public Object[] getParameters() {
        return new Object[] {getActualFlowLauncher(), getFlowToFinish(), getValues()};
    }

    /**
     * Returns the listener for the given method unless the component isn't configured to
     * support listener execution on its own, i.e. someone else needs to trigger them.
     *
     * This is controlled from the removeListeners parameter.
     * @param name
     * @return the listener for the given method
     */
    public IActionListener listener(String name) {
        if (getRemoveListeners()) {
            return null;
        } else {
            return getListeners().getListener(name);
        }
    }

    public boolean isRenderAsDisabled() {
        return isDisabled() || getActualFlowLauncher() == null;
    }

    public String getClassName() {
        if (isParameterBound("updateComponents")) {
            return "fl-entrypoint fl-async "+ObjectUtils.toString(getHtmlClass());
        } else {
            return "fl-entrypoint "+ObjectUtils.toString(getHtmlClass());
        }
    }
}
