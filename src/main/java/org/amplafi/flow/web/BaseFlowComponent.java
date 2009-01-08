package org.amplafi.flow.web;

import static org.apache.commons.lang.StringUtils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.tacos.annotations.Cached;

import org.amplafi.flow.Flow;
import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IForm;
import org.apache.tapestry.TapestryUtils;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.services.ResponseBuilder;


/**
 * A superclass for flow components.
 */
public abstract class BaseFlowComponent extends BaseComponent implements FlowAware {

    /**
     * The components to update when the flow is submitted.
     * If you do NOT specify this, the form that encloses this component
     * will get refreshed (if not found, an exception will be thrown).
     * If you do specify this, you gain full control of what gets refreshed.
     * @return the component list that should be updated when the flow advances or completes.
     */
    @Parameter
    public abstract Object getUpdateComponents();

    @Parameter
    public abstract boolean isDisabled();

    /**
     *
     * Normally, the updateComponents parameter should also be set along with this.
     * If however the flow component resides inside a form, updateComponents can be
     * omitted (in which case the whole form is refreshed).
     *
     * The default value for async is false because there are many flow components that
     * can exist outside forms and would thus require the updateComponents parameters to
     * be set in order to function correctly (leading to more configuration for developers).
     * @return true, if the actions of this flow component should be done asynchronously.
     */
    @Parameter
    public abstract boolean isAsync();

    /**
     * Generates a list of components to update when the flow is submitted.
     * If none specified, the form that encloses this component
     * will get refreshed (if not found, an exception will be thrown).
     * @param comps A hint as to which components we want refreshed.
     * @return The components to refresh.
     */
    public List<String> findComponentsToUpdate(Object comps) {
        List<String> result = asList(comps);
        if ( CollectionUtils.isNotEmpty(result)) {
            return result;
        }
        result = asList(getUpdateComponents());
        if ( CollectionUtils.isNotEmpty(result)) {
            return result;
        } else if (isInsideForm()){
            return Collections.singletonList(getForm().getClientId());
        } else {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> asList(Object object) {
        List<T> result;
        if (object instanceof List) {
            result = (List) object;
        } else if (object instanceof Object[]) {
            result = Arrays.asList((T[]) object);
        } else if (object instanceof String) {
            result = (List<T>) Arrays.asList(object);
        } else {
            result = null;
        }
        return result;
    }
    /**
     * Returns the form that encloses this component.
     *
     * @return the form or null if not in a form.
     * @see org.apache.tapestry.TapestryUtils#getForm(org.apache.tapestry.IRequestCycle, org.apache.tapestry.IComponent)
     */
    @Cached
    public IForm getForm() {
        return TapestryUtils.getForm(getPage().getRequestCycle(), this);
    }
    @Cached
    public boolean isInsideForm() {
        Object attribute = getPage().getRequestCycle().getAttribute( TapestryUtils.FORM_ATTRIBUTE );
        return attribute != null;
    }

    /**
     * Marks the given components as needing update.
     * @param comps
     */
    public void updateComponents(List<String> comps) {
        ResponseBuilder builder = getPage().getRequestCycle().getResponseBuilder();
        for(String compId : comps) {
            builder.updateComponent(compId);
        }
    }

    /**
     *
     * @param labelStr starts with 'message:' if it is a message key.
     *  Otherwise just return labelStr.
     * @param defaultMessageKey  default message key if label is empty or null.
     * @return the label to use.
     */
    protected String processLabel(String labelStr, String defaultMessageKey) {
        if (isBlank(labelStr)) {
            if ( isNotBlank(defaultMessageKey)) {
                return getMessages().getMessage(defaultMessageKey);
            } else {
                return "<<UNKNOWN>>";
            }
        } else if (labelStr.startsWith("message:")) {
            return getMessages().getMessage(labelStr.substring(8));
        } else {
            return labelStr;
        }
    }

    protected String generateDefaultMessageKey(String property) {
        return generateDefaultMessageKey(getAttachedFlowState().getFlow(), property);
    }
    /**
     * generate default message keys where uppercase characters are preceded
     * by a '-' and lowercased.
     *
     * if {@link Flow#getFlowTypeName()} = 'FooBar' and property = 'LinkTitle'
     * then the result is 'flow.foo-bar.link-title'
     * @param flow
     * @param property
     * @return the default message key.
     */
    protected String generateDefaultMessageKey(Flow flow, String property) {
        String flowType = flow.getFlowTypeName();
        StringBuilder sb = new StringBuilder("flow.");
        tweak(flowType, sb);
        sb.append('.');
        tweak(property, sb);
        return sb.toString();
    }

    /**
     * add "-" before each letter that is uppercase and convert uppercase to lowercase.
     * for example, convert "FooBar" to "foo-bar"
     * @param baseName
     * @param sb converted baseName added.
     */
    private void tweak(String baseName, StringBuilder sb) {
        boolean separate = false;
        for(int i = 0; i < baseName.length(); i++) {
            char ch= baseName.charAt(i);
            if (Character.isUpperCase(ch)) {
                if ( separate) {
                    sb.append('-');
                }
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
            separate = true;
        }
    }
}
