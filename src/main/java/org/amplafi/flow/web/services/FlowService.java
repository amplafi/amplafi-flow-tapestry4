/**
 * Copyright 2006-8 by Amplafi, Inc.
 */
package org.amplafi.flow.web.services;

import java.util.Map;

import org.apache.tapestry.engine.IEngineService;

/**
 * Implementors for Tapestry services that start / continue flows.
 *
 */
public interface FlowService extends IEngineService {
    /**
     * Continues a flow.
     *
     * @param flowLookupKey the key of an existing flow to continue
     * @param propertyChanges values with which to update the state of the flow
     */
    public void continueFlowState(String flowLookupKey,
            Map<String, String> propertyChanges);
}
