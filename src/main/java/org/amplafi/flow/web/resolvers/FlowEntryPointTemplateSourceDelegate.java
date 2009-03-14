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


import org.amplafi.flow.Flow;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.parse.ComponentTemplate;

/**
 * @author patmoore
 *
 */
public class FlowEntryPointTemplateSourceDelegate extends FlowTemplateSourceDelegate {

    /**
     * @see org.amplafi.flow.web.resolvers.FlowTemplateSourceDelegate#constructTemplateInstance(org.apache.tapestry.IRequestCycle, char[], org.apache.hivemind.Resource, org.apache.tapestry.IComponent)
     */
    @Override
    protected ComponentTemplate constructTemplateInstance(IRequestCycle cycle, char[] charArray, Resource specificationLocation, IComponent component) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.amplafi.flow.web.resolvers.FlowTemplateSourceDelegate#createComponentTemplate(org.amplafi.flow.Flow, org.apache.tapestry.IRequestCycle, org.apache.tapestry.INamespace, org.apache.hivemind.Location)
     */
    @Override
    protected String createComponentTemplate(Flow flow, IRequestCycle cycle, INamespace namespace, Location location) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String createPageTemplate(Flow flow, IRequestCycle cycle, INamespace namespace, Location location) {
        throw new UnsupportedOperationException();
    }
}
