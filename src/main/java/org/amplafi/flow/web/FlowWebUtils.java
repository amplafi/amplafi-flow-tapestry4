package org.amplafi.flow.web;

import org.amplafi.flow.FlowManagement;
import org.amplafi.flow.FlowState;
import org.amplafi.flow.launcher.FlowLauncher;
import org.amplafi.flow.launcher.StartFromDefinitionFlowLauncher;
import org.amplafi.flow.validation.FlowValidationException;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.engine.ILink;


/**
 * Tapestry related utilities for interacting with flows.
 */
public class FlowWebUtils {

    public static void startFlow(String flowName, Iterable<String> initialValues,
                                 FlowManagement flowManagement, IRequestCycle cycle) {
        String page = null;
        FlowLauncher flowLauncher = new StartFromDefinitionFlowLauncher(flowName, null, initialValues, flowManagement, null);
        try {
            FlowState flowState = flowLauncher.call();
            page = (flowState != null) ? flowState.getCurrentPage() : null;
        } catch (FlowValidationException e) {
            // TODO
            //getFlowResultHandler().handleValidationTrackings(e.getTrackings(), this);
        }
        if (page!=null) {
            ILink link = cycle.getEngine().getInfrastructure().getServiceMap().getService("page").getLink(false, page);
            throw new RedirectException(link.getAbsoluteURL());
        }
    }

    public static String getBlockName(int activity) {
        return "fc" + activity;
    }
    public static String getFlowComponentName(int counter) {
        return "fic" + counter;
    }

    /**
     * if page is not null then activate the page. Also if the page is a IExternalPage, the {@link org.apache.tapestry.IExternalPage#activateExternalPage(Object[], org.apache.tapestry.IRequestCycle)}
     * is called with the current flow as the first element of the passed array.
     *
     * This allows the {@link org.amplafi.flow.FlowState} to be continued across {@link org.apache.tapestry.IExternalPage} calls.
     *
     * It also allows the destination page to start a new flow using the existing state as the starting point.
     *
     * @param cycle can be null
     * @param page can be the name of a page or an arbitrary uri.
     * @param flowState TODO
     */
    public static void activatePageIfNotNull(IRequestCycle cycle, String page, FlowState flowState) {
        if ( page != null ) {

            if (page.startsWith("http")) {
                redirect(cycle, page, "redirect");

            } else if (page.startsWith("client:")) {
                // instructs client to close current window
                String realPage = page.substring("client:".length());
                redirect(cycle, realPage, "close");

            } else {
                if (cycle==null) {
                    throw new PageRedirectException(page);
                } else {
                    IPage appPage = cycle.getPage(page);
                    if ( appPage instanceof IExternalPage) {
                        ((IExternalPage)appPage).activateExternalPage(new Object[] {flowState}, cycle);
                    }
                    cycle.activate(appPage);
                }
            }
        }
    }

    private static void redirect(IRequestCycle cycle, String page, String category) {
        if (cycle==null || !cycle.getResponseBuilder().isDynamic()) {
            throw new RedirectException(page);
        } else {
            cycle.getResponseBuilder().addStatusMessage(null, category, page);
        }
    }
}