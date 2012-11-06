/**
 * Exporter 06.11.2012
 *
 * @author Philipp Haussleiter
 *
 */
package play.modules.i18ned;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import play.Logger;

public class Exporter {

    private final static String EXCEL_OUTPUT_FILE = "tmp" + File.separator + "i18n.xls";
    private Set<Object> messageKeys = new HashSet<Object>();
    private Map<Object, String> messageDescriptions = new HashMap<Object, String>();
    private Map<String, MessageProperties> messageContent = new HashMap<String, MessageProperties>();

    public Exporter() {}

    public static void main(String[] args) {
        Exporter exp = new Exporter();
        exp.scan();
        //exp.debug();
        exp.write();
    }

    public void scan() {
        try {
            for (MessageFile mf : MessagesScanner.scan()) {
                MessageProperties props = MessagesScanner.readMessageProperties(mf.file);
                messageContent.put(mf.language, props);
                messageKeys.addAll(props.keySet());
                messageDescriptions.putAll(props.descriptionSet());
            }
        } catch (IOException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        }
    }

    public void write() {
        try {
            MessageProperty prop;
            String keyString;
            int rowCount = 0;
            int cellCount = 0;
            Workbook wb = new HSSFWorkbook();
            Sheet sheet = wb.createSheet("i18n");
            Row header, row;
            header = sheet.createRow(rowCount++);
            header.createCell(cellCount++).setCellValue("description");
            header.createCell(cellCount++).setCellValue("Key");
            for (String lang : messageContent.keySet()) {
                header.createCell(cellCount++).setCellValue(lang);
            }
            for (Object key : messageKeys) {
                cellCount = 0;
                keyString = (String) key;
                row = sheet.createRow(rowCount++);
                row.createCell(cellCount++).setCellValue(messageDescriptions.get(key));
                row.createCell(cellCount++).setCellValue(keyString);
                for (MessageProperties props : messageContent.values()) {
                    prop = props.getProperty(keyString);
                    if (prop == null) {
                        row.createCell(cellCount++).setCellValue("");

                    } else {
                        row.createCell(cellCount++).setCellValue(prop.getValue());

                    }
                }
            }
            FileOutputStream fileOut = new FileOutputStream(EXCEL_OUTPUT_FILE);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception ioe) {
            Logger.error(ioe, ioe.getLocalizedMessage());
        }
    }

    public void debug() {
        String keyString;
        StringBuilder sb = new StringBuilder("\n");
        sb.append("\tkey");
        for (String lang : messageContent.keySet()) {
            sb.append("\t ").append(lang);
        }
        sb.append("\n");
        for (Object key : messageKeys) {
            keyString = (String) key;
            sb.append("\t").append(keyString);
            for (MessageProperties props : messageContent.values()) {
                sb.append("\t").append(props.getProperty(keyString));
            }
            sb.append("\n");
        }
        Logger.info(sb.toString());
    }
}
