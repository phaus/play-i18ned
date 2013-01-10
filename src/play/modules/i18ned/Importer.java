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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.DateFormatter;
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
    protected Map<String, Integer> headerMapping;
    protected Map<Integer, MessageProperties> messageContent = new HashMap<Integer, MessageProperties>();

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

    /**
     * Writing the MessageProperties into the conf/messages.XX Files.
     */
    public void write() {
        int langId;
        StringBuilder sb = new StringBuilder("writing messages:").append("\n");
        BufferedWriter writer;
        try {
            for (MessageFile mf : MessagesScanner.scan()) {
                langId = headerMapping.get(mf.language);
                sb.append("\t").append("writing file: ").append(mf.file.getName()).append("\n");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mf.file.getAbsolutePath()), "UTF-8"));
                writingHeader(writer, mf.language);
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
        Logger.info(sb.toString());
    }

    /**
     * Reads an excel file into Properties.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void read() throws FileNotFoundException, IOException {
        Logger.info("reading file: " + EXCEL_INPUT_FILE);
        InputStream fileInputStream = new FileInputStream(EXCEL_INPUT_FILE);
        HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
        HSSFSheet sheet = workbook.getSheet("i18n");
        Row row;
        String description, value;
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
                    description = cell1.getStringCellValue();
                    value = cell3.getStringCellValue();
                    if (value.endsWith(MessageProperties.EMPTY_ENTRY)) {
                        description += " "+MessageProperties.EMPTY_ENTRY;
                    }
                    messageContent.get(cc).setProperty(
                            new MessageProperty(
                            cell2.getStringCellValue(), // KEY
                            value, // VALUE
                            description // DESCRIPTION
                            ), cell2.getStringCellValue());
                }
            }
        }
    }

    private void writingHeader(BufferedWriter writer, String language) throws IOException {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        writer.write("# You can specialize this file for each language.\n");
        writer.write("# For example, for French create a messages.fr file.\n");
        writer.write("# This is the File for lang '" + language + "'.\n");
        writer.write("# This File was generated '" + df.format(new Date()) + "'.\n");
        writer.write("# A typical Entry looks like this:\n# #comment\n# key=value\n#\n");
        writer.write("# If you want to mark an entry as not used/emtpy, add "+MessageProperties.EMPTY_ENTRY+" to the end of a comment.\n");
        writer.write("# like this:\n# #comment "+MessageProperties.EMPTY_ENTRY+"\n# key=\n#\n");
        writer.write("#\n");
    }
}