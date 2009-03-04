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

package org.amplafi.flow.web.resolvers;

import java.util.Locale;

import org.amplafi.flow.Flow;
import org.amplafi.flow.FlowDefinitionsManager;
import org.amplafi.flow.web.components.FullFlowComponent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.ITemplateSourceDelegate;
import org.apache.tapestry.parse.ComponentTemplate;
import org.apache.tapestry.spec.IComponentSpecification;

/**
 * @author patmoore
 *
 */
public abstract class FlowTemplateSourceDelegate implements ITemplateSourceDelegate {

    private FlowDefinitionsManager flowDefinitionsManager;
    private Log log;
    public ComponentTemplate findTemplate(IRequestCycle cycle, IComponent component, Locale locale) {
        ComponentTemplate ret = null;
        IComponentSpecification spec = component.getSpecification();
        if (spec.getComponentClassName().equals(FullFlowComponent.class.getName())) {
            String type = spec.getDescription();
            Flow flow = flowDefinitionsManager.getFlowDefinition(type);
            // build the content for this full flow component
            String content;
            if (flow == null) {
                content = "<div>[Flow " + type + " not found]</div>";
            } else if ( CollectionUtils.isEmpty(flow.getActivities())) {
                content = "<div>[Flow " + type + " has no activites]</div>";
            } else {
                content = createTemplate(flow, cycle, component.getNamespace(), component.getLocation());
            }
            // now that we have the content, enhance the location assigned to the spec
            spec.setLocation(new MemoryMappedLocation(spec.getLocation(), content));
            // finally, create the template
            ret = constructTemplateInstance(cycle, content.toCharArray(),
                    spec.getSpecificationLocation(), component);
        }
        return ret;
    }

    /**
     * @param cycle
     * @param charArray
     * @param specificationLocation
     * @param component
     * @return template
     */
    protected abstract ComponentTemplate constructTemplateInstance(IRequestCycle cycle, char[] charArray, Resource specificationLocation, IComponent component);

    /**
     * @param flow
     * @param cycle
     * @param namespace
     * @param location
     * @return template string
     */
    protected abstract String createTemplate(Flow flow, IRequestCycle cycle, INamespace namespace, Location location);

    public void setLog(Log log) {
        this.log = log;
    }

    public Log getLog() {
        return log;
    }

    /**
     * @param flowDefinitionsManager the flowDefinitionsManager to set
     */
    public void setFlowDefinitionsManager(FlowDefinitionsManager flowDefinitionsManager) {
        this.flowDefinitionsManager = flowDefinitionsManager;
    }

    /**
     * @return the flowDefinitionsManager
     */
    public FlowDefinitionsManager getFlowDefinitionsManager() {
        return flowDefinitionsManager;
    }


}
