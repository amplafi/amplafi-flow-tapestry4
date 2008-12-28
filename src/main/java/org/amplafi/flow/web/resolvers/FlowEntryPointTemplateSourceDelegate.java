/**
 * Copyright 2006-2008 by Amplafi. All rights reserved.
 * Confidential.
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
     * @see org.amplafi.flow.web.resolvers.FlowTemplateSourceDelegate#createTemplate(org.amplafi.flow.Flow, org.apache.tapestry.IRequestCycle, org.apache.tapestry.INamespace, org.apache.hivemind.Location)
     */
    @Override
    protected String createTemplate(Flow flow, IRequestCycle cycle, INamespace namespace, Location location) {
        throw new UnsupportedOperationException();
    }

}
