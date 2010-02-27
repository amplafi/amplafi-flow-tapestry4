/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package org.amplafi.flow.web;

import static org.apache.commons.lang.StringUtils.*;

import org.amplafi.flow.FlowState;
import org.amplafi.flow.impl.FlowStateImplementor;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;

/**
 * @author patmoore
 *
 */
public class PageProviderImpl implements PageProvider {

    private IRequestCycle cycle;
    /**
     * @see org.amplafi.flow.web.PageProvider#initializePages(org.amplafi.flow.FlowState)
     */
    @Override
    public void initializePages(FlowState flowState) {
        IPage currentCyclePage;
        try {
            currentCyclePage = getCycle().getPage();
        } catch(NullPointerException e) {
            // because of the way cycle is injected - it is impossible to see if the cycle is null.
            // (normal java checks are looking at the proxy object)
            currentCyclePage = null;
        }
        if ( currentCyclePage != null) {
            String pageName = currentCyclePage.getPageName();
            flowState.setDefaultAfterPage(pageName);
            // from Sasha (18-June-2008): if a flow can have different
            // pageNames, we don't set a pageName in the xml. Example of such
            // flow is ConfigureExtServices. Think the best place to set
            // pageName to a flow is here, because here we know the
            // currentCyclePage.
            if (isBlank(flowState.getCurrentPage())) {
                ((FlowStateImplementor)flowState).setCurrentPage(pageName);
            }
        }
    }
    /**
     * @param cycle the cycle to set
     */
    public void setCycle(IRequestCycle cycle) {
        this.cycle = cycle;
    }
    /**
     * @return the cycle
     */
    public IRequestCycle getCycle() {
        return cycle;
    }

}
