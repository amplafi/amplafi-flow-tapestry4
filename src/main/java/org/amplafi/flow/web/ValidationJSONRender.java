package org.amplafi.flow.web;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.json.IJSONWriter;
import org.apache.tapestry.json.JSONObject;

/**
 * A {@link JSONRender} that tracks validation messages.
 */
public class ValidationJSONRender extends JSONRender {

    private String field;
    private String message;

    public ValidationJSONRender(String field, String message) {
        this.field = field;
        this.message = message;
    }

    @SuppressWarnings("unused")
    public void renderComponent(IJSONWriter writer, IRequestCycle cycle) {
        JSONObject obj = new JSONObject();
        obj.put(field, message);

        writer.object().accumulate("validation", obj);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
