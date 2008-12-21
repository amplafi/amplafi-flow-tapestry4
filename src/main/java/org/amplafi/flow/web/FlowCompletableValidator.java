package org.amplafi.flow.web;

import org.amplafi.flow.FlowState;
import org.amplafi.flow.web.components.FlowBorder;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.FormComponentContributorContext;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.ValidationMessages;
import org.apache.tapestry.form.validator.BaseValidator;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidatorException;


public class FlowCompletableValidator extends BaseValidator {

    @SuppressWarnings("unused")
    @Override
    public void validate(IFormComponent field, ValidationMessages messages, Object object)
            throws ValidatorException {
        IRequestCycle cycle = field.getPage().getRequestCycle();
        FlowBorder border = FlowBorder.get(cycle);
        if (border==null) {
            return;
        }
        FlowState flowState = border.getAttachedFlowState();
        if (!flowState.getCurrentActivityFlowValidationResult().isValid()) {
            throw new ValidatorException("Cannot complete activity",
                    new ValidationJSONRender(field.getName(), "Cannot complete activity"),
                    ValidationConstraint.CONSISTENCY);
        }
    }

    @Override
    public boolean getAcceptsNull() {
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public void renderContribution(IMarkupWriter writer, IRequestCycle cycle, FormComponentContributorContext context,
                                   IFormComponent field) {
        /*IForm form = field.getForm();

        JSONObject profile = context.getProfile();

        if (!profile.has(ValidationConstants.CONSTRAINTS)) {
            profile.put(ValidationConstants.CONSTRAINTS, new JSONObject());
        }
        JSONObject cons = profile.getJSONObject(ValidationConstants.CONSTRAINTS);

        accumulateProperty(cons, field.getClientId(),
                new JSONLiteral("[amplafi.validation.validateFlow, \""
                        + form.getClientId() + "\"]"));

        accumulateProfileProperty(field, profile,
                ValidationConstants.CONSTRAINTS, "Invalid values - Cannot complete current activity");*/
    }
}
