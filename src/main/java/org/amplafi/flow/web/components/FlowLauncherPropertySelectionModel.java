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

package org.amplafi.flow.web.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.amplafi.flow.FlowManagement;
import org.amplafi.flow.launcher.FlowLauncher;
import org.amplafi.flow.launcher.StartFromDefinitionFlowLauncher;
import org.apache.hivemind.Messages;
import org.apache.tapestry.form.IPropertySelectionModel;


/**
 * {@link IPropertySelectionModel} that provides generates {@link StartFromDefinitionFlowLauncher} launchers
 * for selection within a {@link org.apache.tapestry.form.PropertySelection}
 * @author Patrick Moore
 */
public class FlowLauncherPropertySelectionModel implements IPropertySelectionModel {

    private List<FlowLauncher> flows;
    private Messages messages;
    public FlowLauncherPropertySelectionModel(FlowManagement flowManagement, Messages messages, String... flowTypeNames) {
        flows = new ArrayList<FlowLauncher>(flowTypeNames.length);
        for(String flowTypeName: flowTypeNames) {
            flows.add(new StartFromDefinitionFlowLauncher(flowTypeName, flowManagement));
        }
        this.messages = messages;
    }
    public FlowLauncherPropertySelectionModel(FlowManagement flowManagement, Map<String, String>defaultInitialState, Messages messages, String... flowTypeNames) {
        flows = new ArrayList<FlowLauncher>(flowTypeNames.length);
        for(String flowTypeName: flowTypeNames) {
            flows.add(new StartFromDefinitionFlowLauncher(flowTypeName, defaultInitialState, flowManagement));
        }
        this.messages = messages;
    }
    @Override
    public String getLabel(int index) {
        String label = flows.get(index).getLinkTitle();
        if (messages != null && label.startsWith("message:")) {
            label = messages.getMessage(label.substring(8));
        }
        return label;
    }

    @Override
    public Object getOption(int index) {
        return flows.get(index);
    }

    @Override
    public int getOptionCount() {
        return flows.size();
    }

    @Override
    public String getValue(int index) {
        return flows.get(index).getFlowTypeName();
    }

    @Override
    @SuppressWarnings("unused")
    public boolean isDisabled(int index) {
        return false;
    }

    @Override
    public Object translateValue(String value) {
        for(FlowLauncher flowLauncher: this.flows) {
            if ( flowLauncher.getFlowTypeName().equals(value)) {
                return flowLauncher;
            }
        }
        return null;
    }

}
