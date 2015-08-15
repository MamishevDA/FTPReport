/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testtest;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.Orientation;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class TestXLS {

    private static WritableWorkbook workbook; // переменная рабочей книги
    public static WritableSheet sheet;
    public static WritableCellFormat arial12BoldFormat;
    public static Label fileLine;

    static void makeFile() throws IOException, WriteException {
        workbook = Workbook.createWorkbook(new File("c:\\test.xls"));
        sheet = workbook.createSheet("the first part", 0);


        WritableFont arial12ptBold =
                new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        arial12BoldFormat = new WritableCellFormat(arial12ptBold);
        arial12BoldFormat.setWrap(true);

        for (int i = 0; i < 110; i++) {
            for (int j = 0; j < 5; j++) {
                if (j == 4) {
                    fileLine = new Label(j, i, "linesdsdsdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" + i + j, arial12BoldFormat);
                } else {
                    fileLine = new Label(j, i, "line" + i + j);
                }
                sheet.addCell(fileLine);
            }

        }

        workbook.write();
        workbook.close();
    }

    public static void main(String[] asr) throws WriteException, IOException {
        TestXLS.makeFile();
    }
}