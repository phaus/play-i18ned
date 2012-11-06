/**
 * MessageFile
 * 06.11.2012
 * @author Philipp Haussleiter
 *
 */
package play.modules.i18ned;

import java.io.File;

public class MessageFile {
    public File file;
    public String language;
    
    public MessageFile(File file, String language){
        this.file = file;
        this.language = language;
    }
}
