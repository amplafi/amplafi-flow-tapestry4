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
