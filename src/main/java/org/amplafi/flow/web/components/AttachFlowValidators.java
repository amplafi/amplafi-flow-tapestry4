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

package org.amplafi.flow.web.components;

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
        implements ValidatableField {

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
