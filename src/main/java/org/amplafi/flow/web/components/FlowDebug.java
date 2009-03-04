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

import org.amplafi.flow.FlowState;
import org.amplafi.flow.web.BaseFlowComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;


/**
 * A component offering debug capabilities for flows.
 */
public abstract class FlowDebug extends BaseFlowComponent {
    @Component(type="FlowValuesMapComponent", bindings= {"flowValuesMap=currentFlowState.flowValuesMap"})
    public abstract FlowValuesMapComponent getFlowValuesMapComponent();

    public abstract FlowState getCurrentFlowState();
    public abstract void setCurrentFlowState(FlowState state);

    @Override
    protected void prepareForRender(IRequestCycle cycle) {
        super.prepareForRender(cycle);
        FlowState state = getAttachedFlowState();
        if (state == null) {
            FlowBorder border = FlowBorder.get(cycle);
            if (border!=null) {
                state = border.getAttachedFlowState();
            }
        }
        if (state==null) {
            throw new IllegalStateException("flowState is null and no FlowBorder component found.");
        }
        setCurrentFlowState(state);
    }
}
