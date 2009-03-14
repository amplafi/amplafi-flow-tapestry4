package org.amplafi.flow.web.resolvers;

import net.sf.tacos.resolvers.ClasspathSpecResolverDelegate;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.spec.ComponentSpecification;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.INamespace;
import org.apache.hivemind.Resource;
import org.apache.hivemind.Location;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.util.ClasspathResource;

/**
 * Creates the template for a tapestry page that will contain a single flow.
 */
public class FlowAwareSpecResolverDelegate extends ClasspathSpecResolverDelegate {

    public static String ID = "FlowPage";

    private String suffix;
    private Class pageClass;

    public FlowAwareSpecResolverDelegate() {
    }

    @Override
    public IComponentSpecification findPageSpecification(IRequestCycle cycle, INamespace namespace, String name) {
        IComponentSpecification spec = super.findPageSpecification(cycle, namespace, name);
        if (spec==null) {
            // see if it's for a page with flow
            if (name.endsWith(suffix)) {
                String flow = name.substring(0, name.length() - suffix.length());
                spec = installFlowComponent(flow, pageClass);
            }
        }
        return spec;
    }

    private IComponentSpecification installFlowComponent(String flowName, Class clazz) {
        String type = flowName + suffix;
        Resource componentResource = new ClasspathResource(
                new DefaultClassResolver(), type);

        Location location = new LocationImpl(componentResource);

        IComponentSpecification spec = new ComponentSpecification();
        spec.setPageSpecification(true);
        spec.setLocation(location);
        spec.setDescription(flowName);
        spec.setSpecificationLocation(componentResource);
        spec.setComponentClassName(clazz.getName());
        spec.setPublicId(FlowAwareSpecResolverDelegate.ID);

        return spec;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Class getPageClass() {
        return pageClass;
    }

    public void setPageClass(Class pageClass) {
        this.pageClass = pageClass;
    }
}
