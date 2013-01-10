/**
 * PrefixedImporter 09.01.2013
 *
 * @author Philipp Haussleiter
 *
 */
package play.modules.i18ned;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import play.Logger;

public class PrefixedImporter extends Importer {

    private String prefix;
    private String dbTranslationDirectory;

    public PrefixedImporter(final String prefix, final String outputDir) {
        super();
        if (prefix != null && outputDir != null) {
            this.prefix = prefix;
            this.dbTranslationDirectory = outputDir;
            Logger.info("prefix: %s", prefix);
            Logger.info("Directory for DB Translations: %s", dbTranslationDirectory);
        }
    }

    public static void main(String[] args) {
        String prefix = null;
        String outputDir = null;
        if (args.length < 4) {
            System.out.println("\nusage:\nplay i18ned:pimport --prefix <prefix> --output <outputDir>\n");
        } else {
            try {
                if (args.length > 1 && args[0].toLowerCase().equals("--prefix")) {
                    prefix = args[1].trim();
                }
                if (args.length > 3 && args[2].toLowerCase().equals("--output")) {
                    outputDir = args[3].trim();
                }
                PrefixedImporter imp = new PrefixedImporter(prefix, outputDir);
                imp.read();
                imp.write();
            } catch (FileNotFoundException ex) {
                Logger.error(ex, ex.getLocalizedMessage());
            } catch (IOException ex) {
                Logger.error(ex, ex.getLocalizedMessage());
            }
        }
    }

    /**
     * Writing the MessageProperties into the conf/messages.XX Files.
     */
    @Override
    public void write() {
        int langId;
        StringBuilder sb = new StringBuilder("writing messages:").append("\n");
        BufferedWriter writer;
        try {
            for (MessageFile mf : MessagesScanner.scan()) {
                langId = headerMapping.get(mf.language);
                sb.append("\t").append("writing file: ").append(mf.file.getName()).append("\n");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getPath(mf)), "UTF-8"));
                writingHeader(writer, mf.language);
                messageContent.get(langId).save(writer, prefix);
                writer.flush();
                writer.close();
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        } catch (FileNotFoundException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        } catch (IOException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        }
        Logger.info(sb.toString());
    }

    private String getPath(final MessageFile mf) {
        File file = new File(dbTranslationDirectory);
        if (file.exists()) {
            return mf.file.getAbsolutePath().replace(MessagesScanner.CONF_DIR, dbTranslationDirectory);
        } else {
            Logger.error("%s does not exists!", dbTranslationDirectory);
            return null;
        }
    }

    private void writingHeader(BufferedWriter writer, String language) throws IOException {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        writer.write("# You can specialize this file for each language.\n");
        writer.write("# For example, for French create a messages.fr file.\n");
        writer.write("# This is the File for lang '" + language + "'.\n");
        writer.write("# This File was generated '" + df.format(new Date()) + "'.\n");
        writer.write("# The i18n PREFIX is " + prefix + "\n");
        writer.write("# You need to add i18n.db.prefix=" + prefix + " to your conf/application.conf'.\n");
        writer.write("# A typical Entry looks like this:\n# #comment\n# key=value\n#\n");
        writer.write("# If you want to mark an entry as not used/emtpy, add " + MessageProperties.EMPTY_ENTRY + " to the end of a comment.\n");
        writer.write("# like this:\n# #comment " + MessageProperties.EMPTY_ENTRY + "\n# key=\n#\n");
        writer.write("#\n");
    }
}
