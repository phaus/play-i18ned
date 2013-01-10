/**
 * MessageProperties 06.11.2012
 *
 * @author Philipp Haussleiter
 *
 */
package play.modules.i18ned;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import play.Logger;

public class MessageProperties {

    public final static String EMPTY_ENTRY = "##empty##";
    public final static String HEADER_KEY = "KEY";
    public final static String HEADER_DESCRIPTION = "DESCRIPTION";
    public Map<Object, String> descriptions;
    public Map<Object, MessageProperty> properties;
    private String[] parts, arr;

    public MessageProperties() {
        descriptions = new TreeMap<Object, String>();
        properties = new TreeMap<Object, MessageProperty>();
    }

    public void load(final BufferedReader reader) {
        try {
            String line, value, description, utf8Line;
            description = null;
            while ((line = reader.readLine()) != null) {
                utf8Line = new String(line.getBytes(), "UTF-8");
                if (utf8Line.startsWith("#")) {
                    description = utf8Line.substring(1);
                } else {
                    parts = splitTwo(utf8Line);
                    value = parts[1];
                    if (description != null && description.endsWith(EMPTY_ENTRY)) {
                        value = EMPTY_ENTRY;
                        description = null;
                    }
                    if (!parts[0].isEmpty()) {
                        properties.put(parts[0], new MessageProperty(parts[0], value, description));
                    }
                    description = null;
                }
            }
        } catch (IOException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        }

    }

    public void save(BufferedWriter writer, final String prefix) {
        MessageProperty prop;
        try {
            for (Object key : properties.keySet()) {
                if (!key.equals(HEADER_KEY)) {
                    prop = properties.get(key);
                    if (prop != null) {
                        writer.write("\n");
                        writer.write("#");
                        writer.write(prop.getDescription());
                        writer.write("\n");
                        if (prefix != null && !prefix.isEmpty()) {
                            writer.write(prefix);
                            writer.write(".");
                        }
                        writer.write(prop.getKey());
                        writer.write("=");
                        writer.write(prop.getValue());
                        writer.write("\n");
                    }
                }
            }
        } catch (IOException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        }
    }

    public void save(BufferedWriter writer) {
        save(writer, null);
    }

    public MessageProperty getProperty(final Object key) {
        return properties.get(key);
    }

    public void setProperty(final MessageProperty property, final Object key) {
        properties.put(key, property);
    }

    public Collection<? extends Object> keySet() {
        return properties.keySet();
    }

    public Map<? extends Object, String> descriptionSet() {
        if (descriptions.isEmpty()) {
            for (Object key : properties.keySet()) {
                descriptions.put(key, properties.get(key).getDescription());
            }
        }
        return descriptions;
    }

    private String[] splitTwo(String line) {
        arr = new String[2];
        int pos = line.indexOf("=");
        if (pos > 0) {
            arr[0] = line.substring(0, pos);
            arr[1] = line.substring(pos + 1);
        } else {
            arr[0] = line;
            arr[1] = "";
        }
        return arr;
    }
}
