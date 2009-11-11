package org.amplafi.flow.web;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRender;
import org.apache.tapestry.valid.IValidationDelegate;

/**
 * Allows customizing FlowBorder by delegating parts of the rendering to the application
 * that uses flows.
 * 
 * TODO: Add render delegator for a MultiSubmit like component (and update FlowBorder files).
 * 
 * @author andyhot
 * @see org.amplafi.flow.web.components.FlowBorder
 */
public interface FlowBorderCustomizer {
	
	IRender getShowErrorsRenderer(IComponent component, IValidationDelegate delegate, boolean hideFieldErrors);
	
	IRender getDefaultButtonRenderer(IComponent component, String buttonId);

}
