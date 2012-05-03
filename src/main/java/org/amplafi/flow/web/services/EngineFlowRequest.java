package org.amplafi.flow.web.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.amplafi.flow.FlowState;
import org.amplafi.flow.impl.BaseFlowRequest;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.web.WebRequest;

import com.sworddance.util.CUtilities;

public class EngineFlowRequest extends BaseFlowRequest {

    private final IRequestCycle cycle;

    private final WebRequest request;

    private final HttpServletRequest httpServletRequest;

    private final String referingUri;

    public EngineFlowRequest(String renderResultDefault, IRequestCycle cycle, WebRequest request, HttpServletRequest httpServletRequest,
        String referingUri) {
        super(renderResultDefault);
        this.cycle = cycle;
        this.request = request;
        this.httpServletRequest = httpServletRequest;
        this.referingUri = referingUri;
    }

    @Override
    public String getParameter(String parameterName) {
        return this.cycle.getParameter(parameterName);
    }

    @Override
    public List<String> getParameterNames() {
        return this.request.getParameterNames();
    }

    @Override
    public String getReferingUri() {
        return this.referingUri;
    }

    @Override
    public Iterable<String> getIterableParameter(String parameterName) {
        //HACK cycle.getParameters returns a glued comma separated string for some reason.
        String[] parameterValues = this.httpServletRequest.getParameterValues(parameterName);
        //				String[] parameterValues = cycle.getParameters(parameterName);
        return CUtilities.isEmpty(parameterValues) ? Collections.<String> emptyList() : Arrays.asList(parameterValues);
    }

    @Override
    public boolean hasFlowState() {
        return false;
    }

    @Override
    public FlowState getFlowState() {
        return null;
    }

}