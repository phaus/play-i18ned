/**
 * MessageProperty 06.11.2012
 *
 * @author Philipp Haussleiter
 *
 */
package play.modules.i18ned;

public class MessageProperty {

    private String key;
    private String value;
    private String description;

    public MessageProperty(final String key, final String value, final String description) {
        this.key = key.trim();
        this.value = value.trim();
        if (description != null) {
            this.description = description.trim();
        } else {
            this.description = "value for " + key.trim();
        }
    }

    public MessageProperty(final String key, final String value) {
        this(key, value, null);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "\n#" + description + "\n" + key + "=" + value + "\n";
    }
}
