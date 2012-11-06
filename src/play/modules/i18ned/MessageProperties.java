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
import java.util.HashMap;
import java.util.Map;
import play.Logger;

public class MessageProperties {

    public Map<Object, String> descriptions;
    public Map<Object, MessageProperty> properties;
    private String[] parts, arr;

    public MessageProperties() {
        descriptions = new HashMap<Object, String>();
        properties = new HashMap<Object, MessageProperty>();
    }

    public void load(final BufferedReader reader) {
        try {
            String line, description, utf8Line;
            description = null;
            while ((line = reader.readLine()) != null) {
                utf8Line = new String(line.getBytes(), "UTF-8");
                if (utf8Line.startsWith("#")) {
                    description = utf8Line.substring(1);
                } else {
                    parts = splitTwo(utf8Line);
                    if (!parts[0].isEmpty()) {
                        properties.put(parts[0], new MessageProperty(parts[0], parts[1], description));
                    }
                    description = null;
                }
            }
        } catch (IOException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        }

    }

    public void save(BufferedWriter writer) {
        MessageProperty prop;
        try {
            for (Object key : properties.keySet()) {
                prop = properties.get(key);
                if (prop != null) {
                    writer.write("\n");
                    writer.write("#");
                    writer.write(prop.getDescription());
                    writer.write("\n");
                    writer.write(prop.getKey());
                    writer.write("=");
                    writer.write(prop.getValue());
                    writer.write("\n");
                }
            }
        } catch (IOException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        }
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
