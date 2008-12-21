package org.amplafi.flow.web;

import org.amplafi.flow.FlowManagement;
import org.amplafi.flow.FlowState;

/**
 * Marks a class that can access flow information.
 */
public interface FlowProvider {
    public FlowManagement getFlowManagement();

    // TODO : Can we just set a default value that points it to the FlowAware#getAttachedFlowState()?
    public FlowState getFlowToUse();
}
