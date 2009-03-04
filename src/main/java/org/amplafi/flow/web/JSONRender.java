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

import org.apache.tapestry.IJSONRender;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.markup.JSONWriterImpl;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * An abstract helper class that bridges {@link IRender} and {@link IJSONRender}
 * interfaces.
 */
public abstract class JSONRender implements IRender, IJSONRender {

    public void render(IMarkupWriter writer, IRequestCycle cycle) {
        StringWriter sw = new StringWriter();

        JSONWriterImpl jsonWriter = new JSONWriterImpl(new PrintWriter(sw));
        renderComponent(jsonWriter, cycle);
        jsonWriter.close();

        writer.printRaw(sw.toString());
    }
}
