/**
 * SqlExporter 27.06.2013
 *
 * @author Philipp Haussleiter
 *
 */
package play.modules.i18ned;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import play.Logger;

public class SqlExporter {

    private final static String SQL_OUTPUT_FILE = "sql" + File.separator + "i18n.sql";
    private final static String SQL_OUTPUT_ADUIT_FILE = "sql" + File.separator + "i18n.audit.sql";
    private String prefix;
    private String lang;
    private Set<Object> messageKeys = new HashSet<Object>();
    private Map<Object, String> messageDescriptions = new HashMap<Object, String>();
    private Map<String, MessageProperties> messageContent = new HashMap<String, MessageProperties>();

    public SqlExporter(final String prefix, final String lang) {
        this.prefix = prefix;
        this.lang = lang;
    }

    public static void main(String[] args) {
        String prefix = null;
        String lang = null;
        if (args.length < 4) {
            System.out.println("\nusage:\nplay i18ned:sql --prefix <prefix> --lang <lang>\n");
        } else {
            if (args.length > 1 && args[0].toLowerCase().equals("--prefix")) {
                prefix = args[1].trim();
            }
            if (args.length > 3 && args[2].toLowerCase().equals("--lang")) {
                lang = args[3].trim();
            }
            SqlExporter exp = new SqlExporter(prefix, lang);
            exp.scan();
            exp.write();
            exp.writeAudit();
        }
    }

    public void scan() {
        StringBuilder sb = new StringBuilder("\n");
        try {
            for (MessageFile mf : MessagesScanner.scan()) {
                sb.append("\tloading Properties from ").append(mf.file.getName()).append("\n");
                MessageProperties props = MessagesScanner.readMessageProperties(mf.file);
                messageContent.put(mf.language, props);
                messageKeys.addAll(props.keySet());
                messageDescriptions.putAll(props.descriptionSet());
            }
        } catch (IOException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        }
        Logger.info(sb.toString());
    }

    public void write() {
        Logger.info("writing file: " + SQL_OUTPUT_FILE);
        try {

            BufferedWriter writer;
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SQL_OUTPUT_FILE), "UTF-8"));
            writerHead(writer, "APP_TRANSLATION");
            writeInserts(writer);
            writer.flush();
            writer.close();
        } catch (Exception ioe) {
            Logger.error(ioe, ioe.getLocalizedMessage());
        }
    }

    public void writeAudit() {
        Logger.info("writing file: " + SQL_OUTPUT_ADUIT_FILE);
        try {

            BufferedWriter writer;
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SQL_OUTPUT_ADUIT_FILE), "UTF-8"));
            writerHead(writer, "AUDIT_TRANSLATION");
            writeAuditInserts(writer);
            writer.flush();
            writer.close();
        } catch (Exception ioe) {
            Logger.error(ioe, ioe.getLocalizedMessage());
        }
    }

    private void writeAuditInserts(BufferedWriter writer) throws IOException {
        int entryCount = 0;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        for (String langKey : messageContent.keySet()) {
            MessageProperties props = messageContent.get(langKey);
            for (MessageProperty prop : props.properties.values()) {
                if (prop != null
                        && !prop.getKey().equals("messages")) {
                    entryCount++;
                    writer.write("\nINSERT INTO AUDIT_TRANSLATION\n"
                            + "(\n"
                            + "  AUDIT_TRANSLATION_ID,\n"
                            + "  USERNAME,\n"
                            + "  APP_TRANSLATION_ID,\n"
                            + "  CHANGE_TIMESTAMP,\n"
                            + "  SQL_ACTION,\n"
                            + "  COUNTRY,\n"
                            + "  LANGUAGE,\n"
                            + "  NAME,\n"
                            + "  VALUE,\n"
                            + "  DESCRIPTION\n"
                            + ")\n"
                            + "VALUES\n"
                            + "(\n"
                            + "  " + entryCount + ",\n"
                            + "  'i18ned',\n"
                            + "  " + entryCount + ",\n"
                            + "  to_date('2013-06-04','YYYY-MM-DD'),\n"
                            + "  'INSERT',\n"
                            + "  '" + lang + "',\n"
                            + "  '" + langKey + "',\n"
                            + "  '" + prefix + "." + prop.getKey() + "',\n"
                            + "  '" + prop.getValue() + "',\n"
                            + "  '" + prop.getDescription() + "'\n"
                            + ");");
                }
            }
        }
        writer.write("\n\n-- wrote " + entryCount + " entries!\n");
    }

    private void writeInserts(BufferedWriter writer) throws IOException {
        int entryCount = 0;
        String value;
        for (String langKey : messageContent.keySet()) {
            MessageProperties props = messageContent.get(langKey);
            for (MessageProperty prop : props.properties.values()) {
                if (prop != null
                        && !prop.getKey().equals("messages")) {
                    entryCount++;
                    if(!prop.getValue().equals(MessageProperties.EMPTY_ENTRY)){
                        value = prop.getValue();
                    } else {
                        value = "";
                    }
                    writer.write("\nINSERT INTO APP_TRANSLATION\n"
                            + "(\n"
                            + "  APP_TRANSLATION_ID,\n"
                            + "  COUNTRY,\n"
                            + "  LANGUAGE,\n"
                            + "  NAME,\n"
                            + "  VALUE,\n"
                            + "  DESCRIPTION\n"
                            + ")\n"
                            + "VALUES\n"
                            + "(\n"
                            + "  " + entryCount + ",\n"
                            + "  '" + lang + "',\n"
                            + "  '" + langKey + "',\n"
                            + "  '" + prefix + "." + prop.getKey() + "',\n"
                            + "  '" + value + "',\n"
                            + "  '" + prop.getDescription() + "'\n"
                            + ");");
                }
            }
        }
        writer.write("\n\n-- wrote " + entryCount + " entries!\n");
    }

    private void writerHead(BufferedWriter writer, String table) throws IOException {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        writer.write("-- SQL Translation Import\n");
        writer.write("-- This File was generated '" + df.format(new Date()) + "'.\n\n");
        writer.write("delete from " + table + ";\n");
        writer.write("\n");
    }
}
