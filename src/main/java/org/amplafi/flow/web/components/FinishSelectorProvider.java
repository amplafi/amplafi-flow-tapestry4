package org.amplafi.flow.web.components;

import org.amplafi.flow.FlowTransition;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.IActionListener;

import java.util.List;

/**
 * Describes needed parameters for implementing components that
 * display multiple finishes.
 */
public interface FinishSelectorProvider {
    @Parameter(required = true)
    FlowTransition getValue();
    void setValue(FlowTransition transition);

    @Parameter(required = true)
    IPropertySelectionModel getModel();

    @Parameter
    IActionListener getListener();

    @Parameter
    IActionListener getAction();

    @Parameter
    List<String> getUpdateComponents();

    /**
     * The client rendered id for the main (default) button. Can be accessed by the container
     * component in order to differentiate that button. 
     */
    String getButtonId();
}
