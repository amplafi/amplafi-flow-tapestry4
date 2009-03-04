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

import java.util.Map;

import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.spec.ComponentSpecification;
import org.apache.tapestry.IRequestCycle;
import org.apache.hivemind.Resource;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.util.ClasspathResource;

import net.sf.tacos.resolvers.ClasspathComponentSpecResolver;


/**
 * A {@link org.apache.tapestry.resolver.ComponentSpecificationResolver} that is
 * maps component type name suffixes to types.
 *
 * Any component that ends in one of the suffixes ( templateMap.values() ) is a
 * suffix-mapped template.
 *
 * Example:
 *   &lt;span jwcid="@fooFullFlow"/&gt; inserts the template for the "foo" flow.
 */
public class FlowComponentSpecResolver extends ClasspathComponentSpecResolver {
    private Map<Class<?>, String> templateMap;

    @Override
    @SuppressWarnings("unused")
    protected IComponentSpecification doCustomSearch(IRequestCycle cycle) {
        for (Map.Entry<Class<?>, String> entry: templateMap.entrySet()) {
            Class<?> clazz = entry.getKey();
            String suffix = entry.getValue();
            if (getType().endsWith(suffix)) {
                return installFlowComponent(clazz, suffix);
            }
        }
        return null;
    }

    private IComponentSpecification installFlowComponent(Class<?> clazz, String suffix) {
        String type = getType();
        Resource componentResource = new ClasspathResource(
                new DefaultClassResolver(), type);

        Location location = new LocationImpl(componentResource);
        String flowName = type.substring(0, type.length() - suffix.length());

        IComponentSpecification spec = new ComponentSpecification();
        spec.setLocation(location);
        spec.setDescription(flowName);
        spec.setSpecificationLocation(componentResource);
        spec.setComponentClassName(clazz.getName());

        return spec;
    }

    /**
     * @param templateMap the templateMap to set
     */
    public void setTemplateMap(Map<Class<?>, String> templateMap) {
        this.templateMap = templateMap;
    }

    /**
     * @return the templateMap
     */
    public Map<Class<?>, String> getTemplateMap() {
        return templateMap;
    }
}
