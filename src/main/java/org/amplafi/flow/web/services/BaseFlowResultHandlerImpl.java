package org.amplafi.flow.web.services;

import java.util.List;

import org.amplafi.flow.FlowState;
import org.amplafi.flow.FlowValidationResult;
import org.amplafi.flow.FlowValidationTracking;
import org.amplafi.flow.web.BaseFlowComponent;
import org.amplafi.flow.web.FlowResultHandler;
import org.apache.hivemind.Messages;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IPage;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidationConstraint;


public class BaseFlowResultHandlerImpl implements FlowResultHandler {

    @Override
    public void handleFlowResult(FlowValidationResult result, BaseFlowComponent component) {
        FlowState currentFlowState = component.getAttachedFlowState();
        component.getFlowManagement().getLog().warn(
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
     * @param messages
     * @return
     */
    protected StringBuilder getCombinedFormattedValidationString(List<FlowValidationTracking> trackings, IComponent component) {
        IPage page = component.getPage();
        Messages messages = page.getMessages();
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
}
