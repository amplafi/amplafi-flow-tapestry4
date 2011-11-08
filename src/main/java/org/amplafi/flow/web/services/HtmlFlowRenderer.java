package org.amplafi.flow.web.services;

import java.io.Writer;

import org.amplafi.flow.FlowConstants;
import org.amplafi.flow.FlowRenderer;
import org.amplafi.flow.FlowState;
import org.amplafi.flow.web.FlowRequest;
import org.amplafi.flow.web.FlowWebUtils;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.services.ResponseBuilder;

public class HtmlFlowRenderer implements FlowRenderer {

	private IRequestCycle cycle;
	
	private ResponseBuilder responseBuilder;
	
	@Override
	public String getRenderResultType() {
		return FlowConstants.HTML;
	}

	@Override
	public void render(FlowState flowState, Writer writer) {
		String page = flowState.getCurrentPage();
		FlowWebUtils.activateAndRenderPageIfNotNull(cycle, page, flowState, responseBuilder);
	}

	@Override
	public void renderError(FlowState flowState, String message, Exception exception, Writer writer) {
		throw new ApplicationRuntimeException(exception);
	}

	@Override
	public void describe(FlowRequest flowRequest) {
		throw new UnsupportedOperationException();
	}

	public IRequestCycle getCycle() {
		return cycle;
	}

	public void setCycle(IRequestCycle cycle) {
		this.cycle = cycle;
	}

    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    public void setResponseBuilder(ResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

}
