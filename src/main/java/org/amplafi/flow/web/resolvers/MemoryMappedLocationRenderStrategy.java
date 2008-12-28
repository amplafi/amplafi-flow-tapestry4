package org.amplafi.flow.web.resolvers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.describe.RenderStrategy;
import org.apache.hivemind.Location;

/**
 * Strategy for describing a {@link org.amplafi.flow.web.resolvers.MemoryMappedLocation}.
 */
public class MemoryMappedLocationRenderStrategy implements RenderStrategy {
    private Map<Class<?>, String> templateMap;
    /**
     * Lines before and after the actual location to display.
     */
    private static final int RANGE = 5;

    public void renderObject(Object object, IMarkupWriter writer, IRequestCycle cycle) {
        Location location = enhancedLocation((Location) object,
                cycle.getInfrastructure().getSpecificationSource().getApplicationNamespace());

        // Always print out the location as a string.
        writer.print(location.toString());

        int lineNumber = location.getLineNumber();

        if (lineNumber < 1) {
            return;
        }

        if (location instanceof MemoryMappedLocation) {
            handleMemoryLocation((MemoryMappedLocation)location, writer, lineNumber);
        }
        else {
            handleLocation(location, writer, lineNumber);
        }
    }

    private void handleLocation(Location location, IMarkupWriter writer, int lineNumber) {
        URL url = location.getResource().getResourceURL();
        if (url == null) {
            return;
        }

        try {
            writeResourceContent(writer, createReaderFromUrl(url), lineNumber);
        } catch (IOException e) {
            // ignore
        }
    }

    private Location enhancedLocation(Location location, INamespace containerNamespace) {
        for(Map.Entry<Class<?>, String>entry:this.templateMap.entrySet()) {
            String suffix = entry.getValue();
            if (location.getResource()!=null
                    && location.getResource().getPath().endsWith(suffix)) {
                String flowCompName = location.getResource().getPath();
                Location flowLocation = containerNamespace.getComponentSpecification(flowCompName).getLocation();
                Location result;
                if (flowLocation ==null || !(flowLocation instanceof MemoryMappedLocation)) {
                    result = location;
                } else {
                    MemoryMappedLocation extra = (MemoryMappedLocation) flowLocation;
                    result = new MemoryMappedLocation(location, extra.getContent());
                }
                location = result;
            }
        }
        return location;
    }

    private void handleMemoryLocation(MemoryMappedLocation location, IMarkupWriter writer, int lineNumber) {
        String content = location.getContent();

        if (content == null) {
            return;
        }

        writeResourceContent(writer,
                createReaderFromString(content),
                lineNumber);
    }

    private LineNumberReader createReaderFromString(String content) {
        return new LineNumberReader(new BufferedReader(new StringReader(content)));
    }

    private LineNumberReader createReaderFromUrl(URL url) throws IOException {
        return new LineNumberReader(new BufferedReader(new InputStreamReader(url.openStream())));
    }

    private void writeResourceContent(IMarkupWriter writer, LineNumberReader reader, int lineNumber)
    {
        try {
            writer.beginEmpty("br");
            writer.begin("table");
            writer.attribute("class", "location-content");

            while(true) {
                String line = reader.readLine();

                if (line == null) {
                    break;
                }

                int currentLine = reader.getLineNumber();

                if (currentLine > lineNumber + RANGE) {
                    break;
                }

                if (currentLine < lineNumber - RANGE) {
                    continue;
                }

                writer.begin("tr");

                if (currentLine == lineNumber) {
                    writer.attribute("class", "target-line");
                }

                writer.begin("td");
                writer.attribute("class", "line-number");
                writer.print(currentLine);
                writer.end();

                writer.begin("td");
                writer.print(line);
                writer.end("tr");
                writer.println();
            }

            reader.close();
            reader = null;
        }
        catch (Exception ex) {
            // Ignore it.
        } finally {
            writer.end("table");
            close(reader);
        }
    }

    private void close(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        }
        catch (IOException ex) {
            // Ignore
        }
    }

    /**
     * @param templateMap the templateMap to set
     */
    public void setTemplateMap(Map<Class<?>, String> templateMap) {
        this.templateMap = templateMap;
    }

    /**
     * @return the templateMap
     */
    public Map<Class<?>, String> getTemplateMap() {
        return templateMap;
    }
}
