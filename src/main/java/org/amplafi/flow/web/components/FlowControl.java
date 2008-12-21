/*
 * Created on Apr 19, 2005
 *
 */
package org.amplafi.flow.web.components;

import java.util.Collections;
import java.util.List;

import net.sf.tacos.annotations.Cached;

import org.amplafi.flow.FlowActivity;
import org.amplafi.flow.FlowState;
import org.amplafi.flow.validation.FlowValidationResult;
import org.amplafi.flow.web.BaseFlowComponent;
import org.amplafi.flow.web.FlowResultHandler;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Message;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.form.LinkSubmit;


/**
 * This component handles a progressive series of steps that a user would
 * sequentially go through to accomplish a task. This is the wizard class that
 * controls how users will flow through a series of tasks. It does not define
 * the actual tasks. That is the purpose of the various subclasses of
 * {@link org.amplafi.flow.Flow}
 *
 * @author Pat Moore
 */
@ComponentClass(allowBody=true, allowInformalParameters=true)
public abstract class FlowControl extends BaseFlowComponent {

    @Component(id="ls", inheritedBindings={"async", "updateComponents"})
    public abstract LinkSubmit getSubmitTab();

    public abstract void setActivityToGo(int index);
    public abstract int getActivityToGo();

    public abstract FlowActivity getCurrentActivity();

    /**
     * @return Loop activity index.
     */
    public abstract int getIndex();

    @Message("flow.unnamed-activity")
    public abstract String getUnnamedActivity();

    @InjectObject("service:amplafi.flow.resultHandler")
    public abstract FlowResultHandler getFlowResultHandler();

    @Cached(resetAfterRewind=true)
    public List<FlowActivity> getActivities() {
        FlowState attachedFlowState = getAttachedFlowState();
        if (attachedFlowState == null) {
            // under some error conditions (don't believe this happens otherwise), there may be no attachedFlowState.
            return Collections.emptyList();
        } else {
            return attachedFlowState.getVisibleActivities();
        }
    }

    public String getActivityTitle() {
        FlowActivity flowActivity = getCurrentActivity();
        String title = flowActivity.getActivityTitle();
        if (title==null) {
            title = getUnnamedActivity();
        }
        return processLabel(title, null);
    }

    /**
     *
     * @return true if the current activity is disabled.
     */
    public boolean isActivityDisabled() {
        return !getCurrentActivity().isActivatable();
    }

    /**
     *
     * @return true if the current activity is clickable.
     */
    public boolean isActivityClickable() {
        return !(isActivityDisabled() || isActiveActivity());
    }

    /**
     *
     * @return true if the current activity is visible.
     */
    public boolean isActivityVisible() {
        return !getCurrentActivity().isInvisible();
    }

    /**
     *
     * @return
     * true if the activity being rendered the active activity (i.e. the activity
     * that the user is currently working on)
     */
    public boolean isActiveActivity() {
        return getCurrentActivity().equals(getAttachedFlowState().getCurrentActivity());
    }

    public IAsset getLeftTabAsset() {
        String name = isActiveActivity() ? "activeLeft" : "inactiveLeft";

        return getAsset(name);
    }

    public IAsset getMidTabAsset() {
        String name = isActiveActivity() ? "activeMid" : "inactiveMid";

        return getAsset(name);
    }

    public IAsset getRightTabAsset() {
        String name = isActiveActivity() ? "activeRight" : "inactiveRight";

        return getAsset(name);
    }

    /**
     * The user has selected one of the activities, switch over to that activity
     * and make it the active activity.
     *
     */
    public void selectActivity() {
        if (getForm().getDelegate().getHasErrors()) {
            return;
        }

        FlowState currentFlow = getAttachedFlowState();
        FlowValidationResult result = currentFlow.getCurrentActivityFlowValidationResult();
        if (result==null || result.isValid()) {
            FlowActivity next = currentFlow.selectVisibleActivity(getActivityToGo());
            String page = next.getPageName();
            if (page!=null) {
                getPage().getRequestCycle().activate(page);
            } else {
                updateComponents(findComponentsToUpdate(getUpdateComponents()));
            }
        } else {
            getFlowResultHandler().handleFlowResult(result, this);
        }
    }

    public void markActivity(int index) {
        setActivityToGo(index);
    }

    /**
     * @return Returns the activeActivity.
     */
    public int getActiveActivityIndex() {
        FlowState currentFlow = getAttachedFlowState();
        return currentFlow.getCurrentActivityIndex();
    }

    /**
     * @return the submitted page's flowId
     */
    public String getSubmittedFlowId() {
        FlowState currentFlow = getAttachedFlowState();
        // to be returned as part of form submit (see selectActivity)
        return currentFlow.getLookupKey();
    }

    public String getClassForSpan() {
        return isActiveActivity() ? "fl-tabactive" : "fl-tab";
    }
}
