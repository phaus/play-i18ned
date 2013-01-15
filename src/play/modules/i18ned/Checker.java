/**
 * Checker 15.01.2013
 *
 * @author Philipp Haussleiter
 *
 */
package play.modules.i18ned;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import play.Logger;
import play.libs.IO;
import play.templates.TemplateParser;

public class Checker {

    private final static String VIEWS_FOLDER = "app" + File.separator + "views";
    private Map<String, Set<String>> usedKeys = new HashMap<String, Set<String>>();

    public static void main(String[] args) {
        Checker check = new Checker();
        check.scanTemplates();
        check.checkMessages();
    }

    private void scanTemplates() {
        File root = new File(VIEWS_FOLDER);
        Set<String> templates = new TreeSet<String>();
        Logger.info("scanning: " + root.getAbsolutePath());
        templates = getTemplates(root, templates);
        StringBuilder sb = new StringBuilder("scanning Templates:\n");
        for (String template : templates) {
            sb.append("\t").append(template).append("\n");
            usedKeys = getKeyForTemplate(template, usedKeys);
        }
        sb.append("\n");
        Logger.info(sb.toString());
    }

    private void checkMessages() {
        StringBuilder sb;
        MessageProperties props;
        String keyMString;
        Set<String> userKeysKeys = usedKeys.keySet();
        Set<String> templateFiles;
        try {
            for (MessageFile mf : MessagesScanner.scan()) {
                props = MessagesScanner.readMessageProperties(mf.file);
                sb = new StringBuilder("\nResults for " + mf.language).append(":\n");
                sb.append("\nNOT USED:\n\n");
                for (Object mKey : props.keySet()) {
                    keyMString = (String) mKey;
                    if (!userKeysKeys.contains(keyMString)) {
                        sb.append("\t ").append(keyMString).append("\n");
                    }
                }
                sb.append("\nNOT FOUND:\n\n");
                for (String usedKey : userKeysKeys) {
                    if (!props.keySet().contains(usedKey)) {
                        sb.append("\t ").append(usedKey).append("\n");
                        templateFiles = usedKeys.get(usedKey);
                        if(templateFiles != null){
                            for(String tf : templateFiles){
                                sb.append("\t\t ").append(tf).append("\n");
                            }
                        }
                        sb.append("\n");
                    }
                }
                sb.append("\n");
                Logger.info(sb.toString());
            }
        } catch (IOException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        }
    }

    private static Set<String> getTemplates(final File root, Set<String> templates) {
        if (root.exists()) {
            for (File handler : root.listFiles()) {
                if (handler.isFile()) {
                    templates.add(handler.getAbsolutePath());
                } else {
                    templates = getTemplates(handler, templates);
                }
            }
        }
        return templates;
    }

    private static Map<String, Set<String>> getKeyForTemplate(final String templatePath, Map<String, Set<String>> keys) {
        File template = new File(templatePath);
        boolean doNextScan = true;
        TemplateParser.Token state = null;
        Set<String> templateFiles;
        String messageKey;
        if (template.exists()) {
            String source = IO.readContentAsString(template);
            TemplateParser parser = new TemplateParser(source);
            loop:
            for (;;) {
                if (doNextScan) {
                    state = parser.nextToken();
                } else {
                    doNextScan = true;
                }
                switch (state) {
                    case EOF:
                        break loop;
                    case MESSAGE:
                        messageKey = extractKey(parser.getToken());
                        templateFiles = keys.get(messageKey);
                        if (templateFiles == null) {
                            templateFiles = new TreeSet<String>();
                        }
                        templateFiles.add(templatePath);
                        keys.put(messageKey, templateFiles);
                        break;
                }
            }
        }
        return keys;
    }

    private static String extractKey(final String token) {
        String[] parts = token.split(",");
        if (parts.length > 0) {
            return parts[0].replace("'", "").trim();
        }
        return token.trim();
    }
}
