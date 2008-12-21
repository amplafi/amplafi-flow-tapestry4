package org.amplafi.flow.web.components;

import org.amplafi.flow.web.FlowAware;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.form.AbstractFormComponent;
import org.apache.tapestry.form.ValidatableField;
import org.apache.tapestry.form.ValidatableFieldSupport;
import org.apache.tapestry.valid.ValidatorException;


/**
 * Attaches the passed validators to the current form.
 */
public abstract class AttachFlowValidators extends AbstractFormComponent
        implements ValidatableField, FlowAware {

    @Parameter
    public abstract Object getValidators();

    public abstract ValidatableFieldSupport getValidatableFieldSupport();

    @Override
    protected void renderFormComponent(IMarkupWriter writer, IRequestCycle cycle) {
        getValidatableFieldSupport().renderContributions(this, writer, cycle);
        getForm().addHiddenValue(getClientId(), getClientId(), getForm().getClientId());
    }


    @Override
    protected void rewindFormComponent(IMarkupWriter writer, IRequestCycle cycle) {
        try {
            getValidatableFieldSupport().validate(this, writer, cycle, null);
        }
        catch (ValidatorException e) {
            getForm().getDelegate().record(e);
        }
    }

    @Override
    protected boolean getCanTakeFocus() {
        return false;
    }
}
