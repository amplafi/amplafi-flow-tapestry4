package org.amplafi.flow.web.resolvers;

import org.amplafi.flow.web.components.FullFlowComponent;
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
 * aware of flow components.
 */
public class FlowComponentSpecResolver extends ClasspathComponentSpecResolver {
    public static final String FULL_FLOW_SUFFIX = "FullFlow";

    @Override
    @SuppressWarnings("unused")
    protected IComponentSpecification doCustomSearch(IRequestCycle cycle) {
        if (getType().endsWith(FULL_FLOW_SUFFIX)) {
            return installFlowComponent();
        } else {
            return null;
        }
    }

    private IComponentSpecification installFlowComponent() {
        String type = getType();
        Resource componentResource = new ClasspathResource(
                new DefaultClassResolver(), type);

        Location location = new LocationImpl(componentResource);
        String flowName = type.substring(0, type.length() - FULL_FLOW_SUFFIX.length());

        IComponentSpecification spec = new ComponentSpecification();
        spec.setLocation(location);
        spec.setDescription(flowName);
        spec.setSpecificationLocation(componentResource);
        spec.setComponentClassName(FullFlowComponent.class.getName());

        return spec;
    }
}
