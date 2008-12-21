/**
 * Copyright 2006-8 by Amplafi, Inc.
 */
package org.amplafi.flow.web.services;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.join;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.amplafi.flow.Flow;
import org.amplafi.flow.FlowActivity;
import org.amplafi.flow.FlowDefinitionsManager;
import org.amplafi.flow.FlowManagement;
import org.amplafi.flow.FlowState;
import org.amplafi.flow.ServicesConstants;
import org.amplafi.flow.validation.FlowValidationException;
import org.apache.commons.logging.Log;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.services.LinkFactory;
import org.apache.tapestry.util.ContentType;
import org.apache.tapestry.web.WebResponse;


/**
 * Base implementation for Tapestry services that start / continue flows.
 *
 */
public abstract class BaseFlowService implements FlowService {
    public static final String FLOW_ID = "fid";
    protected static final String ADVANCE_TO_END = "advance";
    protected static final String AS_FAR_AS_POSSIBLE = "afap";
    /**
     * advance through the flow until either the flow completes or
     * the current {@link FlowActivity} is named with the advanceTo value.
     *
     * In future it may be considered an error to not have a matching {@link FlowActivity} name
     */
    public static final String ADV_FLOW_ACTIVITY = "fsAdvanceTo";
    /**
     * "advance" --> go through all remaining FlowActivities until the flow completes.
     * "asap" --> advance flow until it can be completed.
     */
    public static final String COMPLETE_FLOW = "fsCompleteFlow";
    private static final String SCRIPT_CONTENT_TYPE = "text/javascript";
    public static final String FLOW_STATE_JSON_KEY = "flowState";
    private LinkFactory linkFactory;
    private FlowDefinitionsManager flowDefinitionsManager;
    private WebResponse response;
    private HttpServletRequest httpServletRequest;
    private Log log;
    private String name;
    /**
     * if {@link #COMPLETE_FLOW} is not supplied - this is the default value to use.
     */
    protected String defaultComplete;
    protected String renderResultDefault;
    protected boolean discardSessionOnExit;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    protected FlowState getFlowState(IRequestCycle cycle, String flowType, String flowId, String renderResult, Map<String, String> initial) throws IOException {
        FlowState flowState = null;
        if ( isNotBlank(flowId)) {
            flowState = getFlowManagement().getFlowState(flowId);
        }
        if ( flowState == null && isNotBlank(flowType)) {

            Flow flow = getFlowDefinitionsManager().getFlowDefinition(flowType);
            if(flow == null) {
                renderError(cycle, flowType+": no such flow type", renderResult, null);
                return null;
            }

            String returnToFlowLookupKey = null;
            flowState = getFlowManagement().startFlowState(flowType, true, initial, returnToFlowLookupKey );
            if ( flowState == null ) {
                renderError(cycle, flowType+": could not start flow type", renderResult, null);
                return null;
            }
        } else {
            renderError(cycle, "neither "+ServicesConstants.FLOW_TYPE+" nor "+FLOW_ID+" in parameters", renderResult, null);
            return null;
        }
        return flowState;
    }
    protected abstract void renderError(IRequestCycle cycle, String message, String renderResult, FlowState flowState) throws IOException;

    public void setLinkFactory(LinkFactory linkFactory) {
        this.linkFactory = linkFactory;
    }

    public LinkFactory getLinkFactory() {
        return linkFactory;
    }

    public void setFlowDefinitionsManager(FlowDefinitionsManager flowDefinitionsManager) {
        this.flowDefinitionsManager = flowDefinitionsManager;
    }

    public FlowDefinitionsManager getFlowDefinitionsManager() {
        return flowDefinitionsManager;
    }

    public FlowManagement getFlowManagement() {
        return getFlowDefinitionsManager().getSessionFlowManagement();
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Log getLog() {
        return log;
    }

    public void setResponse(WebResponse response) {
        this.response = response;
    }

    public WebResponse getResponse() {
        return response;
    }

    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    protected String advanceFlow(FlowState flowState) {
        String error = "some error occured";
        if ( flowState.isCurrentActivityCompletable()) {
            try {
                flowState.next();
                error = null;
            } catch(FlowValidationException flowValidationException) {
                getLog().debug(flowState.getLookupKey(), flowValidationException);
            } catch(Exception e) {
                // TODO attach exception to output somehow.
                getLog().error(flowState.getLookupKey(), e);
                error = flowState.getLookupKey()+" "+e.getMessage()+join(e.getStackTrace(), ", ");
            }
        }
        return error;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ILink getLink(boolean post, Object parameter) {
        if ( parameter instanceof FlowState ) {
            return getLink(post, (FlowState)parameter);
        } else if ( parameter instanceof Map ) {
            Map<String, String> parameterMap = (Map<String, String>) parameter;
            return getLink(post, parameterMap);
        }
        throw new IllegalArgumentException(parameter + "is not a "+ Map.class+ " nor a "+ FlowState.class);
    }

    public ILink getLink(boolean post, FlowState flowState) {
        Map<String,String> map = new HashMap<String, String>();
        map.put(FLOW_ID, flowState.getLookupKey());
        return getLink(post, map);
    }

    public ILink getLink(boolean post, Map<String, String> parameterMap) {
        return getLinkFactory().constructLink(this, post, parameterMap, false);
    }

    public void setDefaultComplete(String defaultComplete) {
        this.defaultComplete = defaultComplete;
    }

    public String getDefaultComplete() {
        return defaultComplete;
    }

    public void setRenderResultDefault(String renderResultDefault) {
        this.renderResultDefault = renderResultDefault;
    }

    public String getRenderResultDefault() {
        return renderResultDefault;
    }

    public void setDiscardSessionOnExit(boolean discardSession) {
        this.discardSessionOnExit = discardSession;
    }

    public boolean isDiscardSessionOnExit() {
        return discardSessionOnExit;
    }

    protected PrintWriter getWriter(IRequestCycle cycle) throws IOException {
        ContentType contentType = new ContentType(SCRIPT_CONTENT_TYPE);

        String encoding = contentType.getParameter("charset");

        if (encoding == null) {
            encoding = cycle.getEngine().getOutputEncoding();
            contentType.setParameter("charset", encoding);
        }
        return getResponse().getPrintWriter(contentType);
    }

    public void continueFlowState(String flowLookupKey, Map<String, String> propertyChanges) throws PageRedirectException {
        FlowState flowState = getFlowManagement().continueFlowState(flowLookupKey, true, propertyChanges);
        if (flowState != null) {
            throw new PageRedirectException(flowState.getCurrentPage());
        }
    }

}
