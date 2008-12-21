package org.amplafi.flow.web;

import net.sf.tacos.annotations.InjectParameterFlag;

import org.amplafi.flow.FlowManagement;
import org.amplafi.flow.FlowState;
import org.apache.tapestry.annotations.InjectMeta;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.annotations.Meta;
import org.apache.tapestry.annotations.Parameter;


/**
 * Marks a page or component that can access flow information.
 */
public interface FlowAware {
    @InjectState(FlowManagement.USER_INFORMATION)
    public FlowManagement getFlowManagement();

    /**
     * @return the {@link FlowState} attached to this component.
     */
    @Parameter
    public FlowState getAttachedFlowState();

    @InjectParameterFlag
    public boolean isAttachedFlowStateBound();

    /**
     * {@link Meta} "requires-flow" should be set to false|true|redirect-page-if-no-active-flow
     * default is home page.
     * @return {@link Meta} "requires-flow" value.
     */
    @InjectMeta("requires-flow")
    public abstract String getRequiresFlowValue();
}
