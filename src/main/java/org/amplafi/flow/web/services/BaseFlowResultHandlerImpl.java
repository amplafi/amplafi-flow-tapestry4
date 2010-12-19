package org.amplafi.flow.web.services;

import java.util.List;

import org.amplafi.flow.FlowState;
import org.amplafi.flow.validation.FlowResultHandler;
import org.amplafi.flow.validation.FlowValidationResult;
import org.amplafi.flow.validation.FlowValidationTracking;
import org.amplafi.flow.web.BaseFlowComponent;

import org.apache.commons.logging.Log;
import org.apache.hivemind.Messages;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidationConstraint;

/**
 * Intended to be configured as a service.
 * @author patmoore
 *
 */
public class BaseFlowResultHandlerImpl implements FlowResultHandler<BaseFlowComponent> {

	private Log log;
    @Override
    public void handleFlowResult(FlowValidationResult result, BaseFlowComponent component) {
        FlowState currentFlowState = component.getAttachedFlowState();
        getLog().warn(
                currentFlowState+" could not complete "+currentFlowState.getCurrentActivityByName()+ " (activity #"+currentFlowState.getCurrentActivityIndex()+ ") flowValidationResult="+result);
        handleValidationTrackings(result.getTrackings(), component);
    }

    @Override
    public void handleValidationTrackings(List<FlowValidationTracking> trackings, BaseFlowComponent component) {

        StringBuilder sb = getCombinedFormattedValidationString(trackings, component);

        recordValidationMessageToForm(component, sb);
    }

    /**
     * @param component
     * @param combinedMessages
     */
    protected void recordValidationMessageToForm(BaseFlowComponent component, StringBuilder combinedMessages) {
        IValidationDelegate delegate = component.getForm().getDelegate();
        delegate.setFormComponent(null);
        delegate.record(combinedMessages.toString(), ValidationConstraint.CONSISTENCY);
    }

    /**
     * @param trackings
     * @param component
     * @return stringbuilder with messages
     */
    protected StringBuilder getCombinedFormattedValidationString(List<FlowValidationTracking> trackings, BaseFlowComponent component) {
        Messages messages = component.getMessages();
        StringBuilder sb = new StringBuilder();
        for (FlowValidationTracking tracking : trackings) {
            handle(tracking, messages, sb);
        }
        return sb;
    }

    protected void handle(FlowValidationTracking tracking, Messages messages, StringBuilder sb) {
        String formattedMessage = messages.format(tracking.getMessageKey(), tracking.getMessageParameters());
        sb.append(formattedMessage);
    }

	public void setLog(Log log) {
		this.log = log;
	}

	public Log getLog() {
		return log;
	}
}
