/**
 * Copyright 2006-2008 by Amplafi. All rights reserved.
 * Confidential.
 */
package org.amplafi.flow.web;

import java.util.List;

/**
 * @author patmoore
 *
 */
public interface FlowAwarePage {
    public abstract boolean isNoExpectedFlowStarted();
    /**
     * @see #isNoExpectedFlowStarted()
     * @return the array of expectedFlowDefinitions for this page.
     */
    public List<String> getExpectedFlowDefinitions();
}
