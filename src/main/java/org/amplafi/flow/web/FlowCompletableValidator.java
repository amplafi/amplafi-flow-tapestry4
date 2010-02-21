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

    @Override
    public void validate(IFormComponent field, ValidationMessages messages, Object object)
            throws ValidatorException {
        IRequestCycle cycle = field.getPage().getRequestCycle();
        FlowAware border = FlowBorder.get(cycle);
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
