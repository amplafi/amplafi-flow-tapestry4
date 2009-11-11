package org.amplafi.flow.web.services;

import org.amplafi.flow.web.FlowBorderCustomizer;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.valid.IFieldTracking;
import org.apache.tapestry.valid.IValidationDelegate;

public class DefaultFlowBorderCustomizer implements FlowBorderCustomizer {

	@Override
	public IRender getDefaultButtonRenderer(final IComponent component, final String buttonId) {		
		return new IRender() {

			@Override
			public void render(IMarkupWriter writer, IRequestCycle cycle) {
				// do nothing
			}
			
		};
	}

	@Override
	public IRender getShowErrorsRenderer(final IComponent component, final IValidationDelegate delegate, 
			final boolean hideFieldErrors) {
		return new IRender() {

			@Override
			public void render(IMarkupWriter writer, IRequestCycle cycle) {
				if (cycle.isRewinding()) {
					return;
				}
				
				if (delegate.getHasErrors()) {
					boolean found = false;
					for (Object item : delegate.getFieldTracking()) {
						IFieldTracking tracking = (IFieldTracking) item;
						if (tracking.isInError() && (!hideFieldErrors || tracking.getComponent()==null)) {
							if (!found) {
								found = true;
								writer.begin("div");
								writer.attribute("class", "warn hideable");
							}							
							tracking.getErrorRenderer().render(writer, cycle);
							writer.beginEmpty("br");
						}						
					}
					if (found) {
						writer.end("div");
					}
				}
			}
			
		};
	}

}
