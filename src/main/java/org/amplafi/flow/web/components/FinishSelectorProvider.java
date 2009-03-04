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

import org.amplafi.flow.FlowTransition;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.IActionListener;

import java.util.List;

/**
 * Describes needed parameters for implementing components that
 * display multiple finishes.
 */
public interface FinishSelectorProvider {
    @Parameter(required = true)
    FlowTransition getValue();
    void setValue(FlowTransition transition);

    @Parameter(required = true)
    IPropertySelectionModel getModel();

    @Parameter
    IActionListener getListener();

    @Parameter
    IActionListener getAction();

    @Parameter
    List<String> getUpdateComponents();

    /**
     * The client rendered id for the main (default) button. Can be accessed by the container
     * component in order to differentiate that button.
     */
    String getButtonId();
}
