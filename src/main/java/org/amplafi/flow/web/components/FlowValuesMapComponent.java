/**
 * Copyright 2006-2008 by Amplafi. All rights reserved.
 * Confidential.
 */
package org.amplafi.flow.web.components;

import java.util.Map;
import java.util.Set;

import net.sf.tacos.annotations.Cached;

import org.amplafi.flow.FlowValuesMap;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;


/**
 * Display the values of a {@link FlowValuesMap}.
 *
 */
public abstract class FlowValuesMapComponent extends BaseComponent {

    @Parameter(required=true)
    public abstract FlowValuesMap getFlowValuesMap();
    public abstract String getPropertyName();

    public Set<String> getFlowPropertyNames() {
        return getStringMap().keySet();
    }
    /**
     * @return {@link #getFlowValuesMap()} as a string map
     */
    @Cached(resetAfterRewind=true)
    private Map<String, String> getStringMap() {
        return getFlowValuesMap().getAsFlattenedStringMap();
    }

    public String getPropertyValue() {
        return getStringMap().get(getPropertyName());
    }
}
