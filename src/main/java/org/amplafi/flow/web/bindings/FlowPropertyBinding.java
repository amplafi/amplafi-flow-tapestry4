/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package org.amplafi.flow.web.bindings;

import org.amplafi.flow.FlowActivity;
import org.amplafi.flow.FlowActivityPhase;
import org.amplafi.flow.FlowPropertyDefinition;
import org.amplafi.flow.FlowState;
import org.amplafi.flow.FlowStateProvider;

import com.sworddance.util.ApplicationIllegalStateException;
import com.sworddance.util.perf.LapTimer;

import org.apache.commons.logging.Log;
import org.apache.hivemind.Location;
import org.apache.tapestry.BindingException;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.binding.BindingConstants;
import org.apache.tapestry.binding.BindingFactory;
import org.apache.tapestry.binding.BindingSource;
import org.apache.tapestry.binding.LiteralBinding;
import org.apache.tapestry.coerce.ValueConverter;
import org.apache.tapestry.form.AbstractFormComponent;
import org.apache.tapestry.form.ValidatableField;
import org.apache.tapestry.valid.ValidatorException;

import static org.apache.commons.lang.StringUtils.*;
import static com.sworddance.util.ApplicationNullPointerException.*;

/**
 * An implementation of {@link org.apache.tapestry.IBinding} to connect components to flow properties.
 * This binding is used automatically by the {@link org.amplafi.flow.web.resolvers.FlowAwareTemplateSourceDelegate}
 * to connect the generated flow template (and the components in the flow) to the flow properties.
 *
 * Most of the time developers are not aware nor do they explicitly use this {@link org.apache.tapestry.IBinding}.
 *
 * Putting a breakpoint in {@link #getFlowStateProperty(Class)} is a good place to start debugging any issues with
 * parameters of a component not being connected up correctly.
 *
 * @author Patrick Moore
 */
public class FlowPropertyBinding implements FlowStateProvider, IBinding {
    private static final String ALREADY_ADDED_BINDING="_amp_FlowProperty_Binding_Already";
    /**
     *
     */
    private static final String HTML_ONBLUR = "onblur";
    private static final String REQUIRED = "required";
    /**
     *
     */
    private static final String VALIDATORS = "validators";
    private static final String HTML_CLASS = "class";

    private final ValueConverter valueConverter;

    private final Location location;

    /**
     * The flow component that is the source of this binding
     */
    private IComponent root;

    /**
     * The expression used to access the binding
     */
    private String key;

    /**
     * The request cycle for the FlowProvider
     */
    private IRequestCycle cycle;

    /**
     * The Tapestry binding factory used to create simple, non-flow bindings
     */
    private BindingFactory validationBindingFactory;

    private String defaultValue;

    private String description;

    private String componentName;

    private IBinding defaultValueBinding;

    private Log log;
    /**
     * Constructor - Set to protected to ensure the use of the {@link FlowPropertyBindingFactory} in creation of this object.
     *
     * @param root The flow component that is the source of this binding
     * @param description A description of how the binding is used
     * @param valueConverter Used to convert the value of the binding to a specific data type
     * @param location The location of the binding
     * @param expression The expression used to access the binding
     * @param bindingFactory The Tapestry binding factory used to create simple, non-flow bindings
     * @param bindingSource TODO
     * @throws IllegalArgumentException If the expression is not populated
     */
    protected FlowPropertyBinding(IComponent root, String description, ValueConverter valueConverter, Location location, String expression,
            BindingFactory bindingFactory, BindingSource bindingSource) {

        notNull(description, "description");
        notNull(valueConverter, "valueConverter");

        this.valueConverter = valueConverter;
        this.location = location;
        this.description = description;
        // Save instance variables
        this.root = root;

        notNull(expression, this,":no expression to evaluate");
        int equalsIndex = expression.indexOf('=');
        // also check to make sure the '=' is not the last character.
        if (equalsIndex >= 0 && equalsIndex < expression.length()-1) {
            this.key = expression.substring(0, equalsIndex);
            // the expression came in the form "fprop:key=some-default-value"
            int componentIndicator = expression.indexOf('@', equalsIndex+1);
            if ( componentIndicator > equalsIndex) {
                componentName = expression.substring(equalsIndex+1, componentIndicator);
                this.defaultValue = expression.substring(componentIndicator+1);
            } else {
                this.defaultValue = expression.substring(equalsIndex+1);
            }
            IComponent flowComponent;
            if ( isNotBlank(componentName)) {
                flowComponent = root.getComponent(componentName);
            } else {
                flowComponent = root;
            }
            defaultValueBinding = bindingSource.createBinding(flowComponent,
                                                              description+ " default binding. Component Name ='"+componentName+"' defaultValue='"+defaultValue+"'", defaultValue,
                                                              BindingConstants.OGNL_PREFIX, location);
        } else {
            this.key = expression;
            // just for explicitness
            this.defaultValue = null;
        }
        this.cycle = root.getPage().getRequestCycle();
        this.validationBindingFactory = bindingFactory;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Returns a value indicating whether or not the value of this binding is invariant or not. This implementation will always return {@code false}
     * because at this time flow properties can always be modified.
     *
     * @return A boolean value indicating whether or not this binding's value is invariant
     */
    @Override
    public boolean isInvariant() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Object getObject(Class type) {
        notNull(type, this, "type");
        // TODO: performance improvement. During cycle.rewinding if the property does not affect an if clause or layout, can we just return some sort of dummy object?
        // or something innocent? null might not work.
        // Use case: In MessageEndPointList, the verificationMap is used for determining the html class.
        Object raw = getFlowStateProperty(type);

        try {
            return valueConverter.coerceValue(raw, type);
        } catch (Exception ex) {
            // String message = BindingMessages.convertObjectError(this, ex);

            if (raw != null) {
                throw new BindingException("Error converting a "+raw.getClass().getName()+" to "+type.getName()+" toString() of value ="+raw,
                                           this.root, location, this, ex);
            } else {
                throw new BindingException("Error converting a null", this.root, location, this, ex);
            }
        }
    }

    /**
     * Gets the value of this binding.
     *
     * @return The value of the binding
     * @throws BindingException If there is an issue (validity, well-formed) with the value of the binding
     */
    @Override
    public Object getObject() throws BindingException {
        return getFlowStateProperty(null);
    }

    protected Object getFlowStateProperty(Class<?> expected) {
        Object result = null;
        // Determine if there is a flow state to get the value from, if not just return defaultValue
        FlowState flowState = getFlowState();
        LapTimer.sLap("FlowPropertyBinding: Getting property ",key);
        if (flowState != null) {
            addValidation(flowState.getCurrentActivity(), cycle.renderStackPeek());
            try {
                result = flowState.getProperty(key, expected);
            } catch (RuntimeException e) {
                if (e.getCause() instanceof ValidatorException) {
                    throw new BindingException(e.getMessage(), this, e.getCause());
                } else {
                    throw new BindingException(e.getMessage(), this, e);
                }
            }
        }
        LapTimer.sLap("FlowPropertyBinding: Got property ",key);
        if (result == null && defaultValueBinding != null) {
            result = defaultValueBinding.getObject(expected);
        }
        return result;
    }

    /**
     * Sets the value of the binding, if allowed.
     *
     * @param value The new value of the binding
     * @throws IllegalStateException If there is no flow attached to the binding
     * @throws BindingException If the value is not assignable to the specified type
     */
    @SuppressWarnings("null")
    @Override
    public void setObject(Object value) {
        // Check that we have a flow to set the value to
        FlowState flowState = getFlowState();
        ApplicationIllegalStateException.notNull(flowState, this,": no attached flow - cannot set value");
        flowState.setProperty(key, value);
    }

    /**
     * Specific implementation of the Object.toString method.
     *
     * @return A string representation of this binding
     */
    @Override
    public String toString() {
        return super.toString() + "[expression=" + this.key + (defaultValue==null?"]": (" defaultValue="+this.defaultValue+"]"));
    }

    /**
     * Get the {@link FlowState} object this binding uses.
     *
     * @return The FlowState object this binding uses
     */
    public FlowState getFlowState() {
        return ((FlowStateProvider)this.root).getFlowState();
    }

    /**
     * Important! READ  if validation is not being added OR is added incorrectly ( usually visible because a required field does not have the "required" styling).
     * If this occurs it is because the first time the binding was evaluated is the ONLY time the FlowPropertyBinding gets a chance to add its validation.
     * To solve this make sure the @{@link org.apache.tapestry.annotations.Parameter} has caching turned off.
     *
     * For Example: @{@link org.apache.tapestry.annotations.Parameter}(required=true, cache=false)
     *
     * From Andy 19 mar 2010:
     *
     *
     * - the responses i see are empty, just <ajax-response></ajax-response>.. not yet know why
     *
     * @param activity
     * @param render
     */
    private void addValidation(FlowActivity activity, IRender render) {
        if (render instanceof AbstractFormComponent && render instanceof ValidatableField) {
            AbstractFormComponent formComponent = (AbstractFormComponent) render;
            IBinding alreadyBinding = formComponent.getBinding(ALREADY_ADDED_BINDING);
            if ( alreadyBinding == null) {
                FlowPropertyDefinition definition = activity.getFlowPropertyDefinition(this.key);
                if ( definition != null) {
                    IBinding validatorsBinding = formComponent.getBinding(VALIDATORS);
                    if (definition.isDynamic()) {
                        IBinding htmlClassBinding = formComponent.getBinding(HTML_CLASS);
                        IBinding htmlOnBlurBinding = formComponent.getBinding(HTML_ONBLUR);
                        String htmlClassToAdd = "refresh-" + formComponent.getClientId();
                        String htmlClass = null;
                        if ( htmlClassBinding == null) {
                            htmlClass= htmlClassToAdd;
                        } else if (htmlClassBinding instanceof LiteralBinding) {
                            htmlClass =(String)htmlClassBinding.getObject(String.class) + " "+htmlClassToAdd;
                        } else {
                            getLog().debug(activity.getFullActivityInstanceNamespace()+ ": cannot add class to component="+formComponent);
                        }
                        if (htmlClass != null) {
                            formComponent.setBinding(HTML_CLASS, new LiteralBinding("html class", valueConverter, location, htmlClass));
                        }
                        if (htmlOnBlurBinding == null) {
                            String htmlOnBlurValue = "javascript:amplafi.util.refreshIfChanged(this);";
                            formComponent.setBinding(HTML_ONBLUR, new LiteralBinding("html on blur", valueConverter, location, htmlOnBlurValue));

                        } else {
                            getLog().debug(activity.getFullActivityInstanceNamespace()+ ": cannot add onblur to component="+formComponent);
                        }
                    }
                    if (validatorsBinding == null) {
                        String validators = definition.getValidators();
                        if ( definition.getPropertyRequired() == FlowActivityPhase.advance) {
                            if ( isBlank(validators)) {
                                validators = REQUIRED;
                            } else {
                                // may re-add required if already present - seems like a minor issue
                                validators = REQUIRED + "," + validators;
                            }
                        }
                        if (isNotBlank(validators)) {
                            validatorsBinding = this.validationBindingFactory.createBinding(formComponent, "", validators, null);
                            formComponent.setBinding(VALIDATORS, validatorsBinding);
                        }
                    } else if ( getLog().isDebugEnabled()){
                        getLog().debug(activity.getFullActivityInstanceNamespace()+": property binding "+this+": make sure that @Parameter(cache=false) is set because first access is not by a ValidatableField component");
                    }
                }
                // avoid constantly re-adding the bindings
                // HACK: this however results in adding the attribute to all html nodes using a FlowPropertyBinding.
                formComponent.setBinding(ALREADY_ADDED_BINDING, new LiteralBinding(ALREADY_ADDED_BINDING, valueConverter, location, "true"));
            }
        }
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @return the log
     */
    public Log getLog() {
        return log;
    }
}
