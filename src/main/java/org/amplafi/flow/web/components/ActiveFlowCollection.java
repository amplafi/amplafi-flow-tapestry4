/*
 * Created on Jun 14, 2007
 * Copyright 2006 by Patrick Moore
 */
package org.amplafi.flow.web.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.amplafi.flow.FlowState;
import org.amplafi.flow.launcher.ContinueFlowLauncher;
import org.amplafi.flow.launcher.FlowLauncher;
import org.amplafi.flow.web.BaseFlowComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;


/**
 * Display a collection of flows that are active but not the current flow.
 * @author Patrick Moore
 */
@ComponentClass
public abstract class ActiveFlowCollection extends BaseFlowComponent {

    /**
     * @return
     * Flow types to exclude from presenting.
     */
    @Parameter
    public abstract Collection<String> getExcludedFlowTypes();

    @Component(type="FlowEntryCollection",
            bindings={"flows=notCurrentFlows", "collectionName=message:flow.active-flows"},
            inheritedBindings={"updateComponents","async","disabled"})
    public abstract FlowEntryCollection getFlowEntryCollection();

    /**
     * @return
     * the active flows that are not one of the excluded flow types
     * and is not the current flow.
     */
    public List<FlowLauncher> getNotCurrentFlows() {
        List<FlowState> flows = getFlowManagement().getFlowStates();
        List<FlowLauncher> continuedFlows = new ArrayList<FlowLauncher>();
        if ( flows.size() > 1 ) {
            Collection<String> excludedFlowTypes = getExcludedFlowTypes();
            for(int i = 1; i < flows.size(); i++) {
                if ( excludedFlowTypes == null
                        || !excludedFlowTypes.contains(flows.get(i).getFlowTypeName())) {
                    ContinueFlowLauncher continueFlow = new ContinueFlowLauncher(flows.get(i), getFlowManagement());
                    continuedFlows.add(continueFlow);
                }
            }
        }
        return continuedFlows;
    }
}
