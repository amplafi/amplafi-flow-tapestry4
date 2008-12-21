package org.amplafi.flow.web.components;

import org.apache.tapestry.BaseComponent;

/**
 * Component that displays multiple finishes as a combo box and a submit button.
 */
public abstract class FinishSelector extends BaseComponent implements FinishSelectorProvider {
    @Override
    public String getButtonId() {
        return getComponent("MultipleAltFlow").getClientId();
    }
}
