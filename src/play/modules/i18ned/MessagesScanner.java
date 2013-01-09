/**
 * MessagesScanner 06.11.2012
 *
 * @author Philipp Haussleiter
 *
 */
package play.modules.i18ned;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import play.Logger;

public class MessagesScanner {

    private final static String MESSAGES_FILENAME = "messages";
    private static final String CONF_DIR = "conf" + File.separator;
    private static final MessageFileFilter MESSAGE_FILTER = new MessageFileFilter();

    private MessagesScanner() {}

    public static MessageFile[] scan() {
        StringBuilder sb = new StringBuilder();
        sb.append("scanning Application: ").append(CONF_DIR).append("\n");
        String ext;
        File rootDir = new File(CONF_DIR);
        List<MessageFile> messagesList = new ArrayList<MessageFile>();
        if (rootDir.exists()) {
            for (File mFile : rootDir.listFiles(MESSAGE_FILTER)) {
                sb.append("\tfound ").append(mFile.getName()).append("\n");
                ext = getExtension(mFile);
                messagesList.add(new MessageFile(mFile, ext));
            }
            Logger.info(sb.toString());
            return messagesList.toArray(new MessageFile[messagesList.size()]);
        }
        Logger.info(sb.toString());
        return new MessageFile[0];
    }

    public static MessageProperties readMessageProperties(final File file) throws IOException {
        Charset charset = Charset.forName("UTF-8");
        MessageProperties props = new MessageProperties();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        props.load(br);
        br.close();
        return props;
    }

    private static String getExtension(File file) {
        int dot = file.getName().lastIndexOf(".");
        return file.getName().substring(dot + 1);
    }

    private static class MessageFileFilter implements FileFilter {

        public boolean accept(File file) {
            String name = file.getName();
            if (name != null
                    && !name.startsWith(MESSAGES_FILENAME)
                    && file.isFile()) {
                Logger.debug("Filtering... " + file.getAbsolutePath());
                return false;
            }
            return true;
        }
    }
}
