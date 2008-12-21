package org.amplafi.flow.web.resolvers;

import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;

/**
 * A location that represents a string in memory. 
 */
public class MemoryMappedLocation implements Location {

    private String content;
    private Location delegate;

    public MemoryMappedLocation(Location delegate, String content) {
        this.delegate = delegate;
        this.content = content;
    }

    public Resource getResource() {
        return delegate.getResource();
    }

    public int getLineNumber() {
        return delegate.getLineNumber();
    }

    public int getColumnNumber() {
        return delegate.getColumnNumber();
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "<MemoryMapped>," + delegate.toString();
    }
}
