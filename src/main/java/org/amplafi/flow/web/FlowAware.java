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
    @InjectState("sessionFlows")
    public FlowManagement getFlowManagement();

    /**
     * @param <FS>
     * @return the {@link FlowState} attached to this component.
     */
    @Parameter
    public <FS extends FlowState> FS getAttachedFlowState();

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
