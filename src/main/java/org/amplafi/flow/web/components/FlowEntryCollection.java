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

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.amplafi.flow.launcher.FlowLauncher;
import org.amplafi.flow.launcher.StartFromDefinitionFlowLauncher;
import org.amplafi.flow.web.BaseFlowComponent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;


/**
 * This is a named collection of entry points. For example,
 * "Active Flows", "Recent Flows", or "Available Flows".
 */
@ComponentClass(allowBody=false)
public abstract class FlowEntryCollection extends BaseFlowComponent {

    /**
     * The title of the collection.
     * @return Returns the collectionName.
     */
    @Parameter(required=true)
    public abstract String getCollectionName();

    /**
     * A mix of FlowState and Flow.
     * @return Returns the entryPoints.
     */
    @Parameter
    public abstract List<FlowLauncher> getFlows();

    /**
     * These flow types will have be added to the end of the list supplied
     * in {@link #getFlows()}.
     * @return flowTypes available to launch by default.
     */
    @Parameter
    public abstract List<String> getFlowTypeNames();

    @Parameter
    public abstract Iterator<String> getClassNames();

    @Component(type="FlowEntryPoint",
            bindings= {"flowLauncher=current"},
            inheritedBindings={"updateComponents", "async", "disabled"})
    public abstract FlowEntryPoint getFlowEntryPoint();

    public abstract void setCurrent(FlowLauncher flowLauncher);
    public abstract FlowLauncher getCurrent();

    public List<FlowLauncher> getDisplayedFlows() {
        List<FlowLauncher> displayed = new ArrayList<FlowLauncher>();
        if ( !CollectionUtils.isEmpty(getFlows())) {
            displayed.addAll(getFlows());
        }
        List<String> ftNames = getFlowTypeNames();
        if ( !CollectionUtils.isEmpty(ftNames)) {
            for(String flowTypeName: ftNames) {
                StartFromDefinitionFlowLauncher definition =
                    new StartFromDefinitionFlowLauncher(flowTypeName, null, getFlowManagement());
                displayed.add(definition);
            }
        }
        return displayed;
    }

    public String getDisplayedClass() {
        if (getClassNames()==null) {
            return null;
        } else {
            return getClassNames().next();
        }
    }
}
