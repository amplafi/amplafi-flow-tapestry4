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
