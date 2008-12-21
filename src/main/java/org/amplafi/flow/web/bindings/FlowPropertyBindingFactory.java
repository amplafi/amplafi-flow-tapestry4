/*
 * Created on Jun 3, 2007
 * Copyright 2006 by Patrick Moore
 */
package org.amplafi.flow.web.bindings;

import org.amplafi.flow.web.FlowProvider;
import org.apache.hivemind.Location;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.binding.AbstractBindingFactory;
import org.apache.tapestry.binding.BindingFactory;
import org.apache.tapestry.binding.BindingSource;


/**
 * Factory to create {@link FlowPropertyBinding}s.
 *
 * @author Patrick Moore
 */
public class FlowPropertyBindingFactory extends AbstractBindingFactory {

    /**
     * The Tapestry BindingFactory that the FlowPropertyBinding will use to create bindings
     */
    private BindingFactory validationBindingFactory;
    private BindingSource bindingSource;

    /**
     * Set the {@link BindingFactory} that is used to create a flow binding.
     *
     * @param validationBindingFactory
     */
    public void setValidationBindingFactory(BindingFactory validationBindingFactory) {
        this.validationBindingFactory = validationBindingFactory;
    }

    /**
     * Create a {@link FlowPropertyBinding} with a specific value and within a specific {@link FlowProvider}.
     *
     * @see org.apache.tapestry.binding.BindingFactory#createBinding(IComponent, String, String, Location)
     *
     * @param root The flow component that is the source of the property
     * @param bindingDescription A description of how the binding is used
     * @param expression The expression used to access the binding
     * @param location The location of the binding
     * @return A {@link FlowPropertyBinding} object created with the parameters passed in
     * @throws IllegalArgumentException If the {@code root} parameter does not implement {@link FlowProvider}
     */
    public IBinding createBinding(IComponent root, String bindingDescription,
            String expression, Location location) {

        // Validate that the IComponent passed in is a Flow component
        if(!(root instanceof FlowProvider)) {
            throw new IllegalArgumentException(root.getClass()+
                    ": is required to implement FlowProvider interface");
        }

        return new FlowPropertyBinding( root, bindingDescription, getValueConverter(), location,
                expression, validationBindingFactory, this.bindingSource);
    }

    /**
     * @param bindingSource the bindingSource to set
     */
    public void setBindingSource(BindingSource bindingSource) {
        this.bindingSource = bindingSource;
    }

    /**
     * @return the bindingSource
     */
    public BindingSource getBindingSource() {
        return bindingSource;
    }
}
