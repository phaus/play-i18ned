/**
 * Importer 06.11.2012
 *
 * @author Philipp Haussleiter
 *
 */
package play.modules.i18ned;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import play.Logger;

public class Importer {

    private final static int HEADER_ROW = 0;
    private final static int FIRST_COLUMN = 0;
    private final static int SECOND_COLUMN = 1;
    private final static String EXCEL_INPUT_FILE = "tmp" + File.separator + "i18n.xls";
    private Map<String, Integer> headerMapping;
    private Map<Integer, MessageProperties> messageContent = new HashMap<Integer, MessageProperties>();

    public Importer() {
        headerMapping = new HashMap<String, Integer>();
    }

    public static void main(String[] args) {
        try {
            Importer imp = new Importer();
            imp.read();
            imp.write();
        } catch (FileNotFoundException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        } catch (IOException ex) {
            Logger.error(ex, ex.getLocalizedMessage());
        }
    }

    public void write() {
        int langId;
        BufferedWriter writer;
        try {
            for (MessageFile mf : MessagesScanner.scan()) {
                langId = headerMapping.get(mf.language);
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mf.file.getAbsolutePath()), "UTF-8"));
                messageContent.get(langId).save(writer);
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

    }

    public void read() throws FileNotFoundException, IOException {
        InputStream fileInputStream = new FileInputStream(EXCEL_INPUT_FILE);
        HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
        HSSFSheet sheet = workbook.getSheet("i18n");
        Row row;
        Cell cell1, cell2, cell3;
        for (int rc = sheet.getFirstRowNum(); rc < sheet.getLastRowNum(); rc++) {
            row = sheet.getRow(rc);
            // Header Mapping: lang <=> Column-Number
            if (HEADER_ROW == rc) {
                for (int cc = 2; cc < row.getLastCellNum(); cc++) {
                    cell1 = row.getCell(cc);
                    headerMapping.put(cell1.getStringCellValue(), cc);
                    messageContent.put(cc, new MessageProperties());
                }
            }

            StringBuilder sb = new StringBuilder(rc).append(":");
            cell1 = row.getCell(FIRST_COLUMN);
            cell2 = row.getCell(SECOND_COLUMN);
            for (int cc = 2; cc < row.getLastCellNum(); cc++) {
                cell3 = row.getCell(cc);
                if (cell3 != null) {
                    messageContent.get(cc).setProperty(
                            new MessageProperty(
                            cell2.getStringCellValue(), // KEY
                            cell3.getStringCellValue(), // VALUE
                            cell1.getStringCellValue() // DESCRIPTION
                            ), cell2.getStringCellValue());
                }
            }
        }
    }
}