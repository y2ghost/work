package study.ywork.doc.itext.inside;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PageLabelNumberingStyle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import study.ywork.doc.hsqldb.DatabaseConnection;
import study.ywork.doc.util.DBUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PageLabelExample {
    private static final String DEST = "pageLabelExample.pdf";
    private static final String DEST2 = "pageLabelExample_2.pdf";
    private static final String DEST_TXT = "page_labels.txt";
    private static final String[] SQL = {
            "SELECT country FROM film_country ORDER BY country",
            "SELECT name FROM film_director ORDER BY name",
            "SELECT title FROM film_movietitle ORDER BY title"
    };
    private static final String[] FIELD = {"country", "name", "title"};

    public static void main(String[] args) {
        new PageLabelExample().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        createPdf(dest);
        listPageLabels(dest, DEST_TXT);
        manipulatePageLabel(dest, DEST2);
    }

    private void createPdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc, PageSize.A5)) {
            DatabaseConnection connection = DBUtils.getFilmDBConnection();
            int[] start = new int[3];
            pdfDoc.addNewPage();

            for (int i = 0; i < 3; i++) {
                start[i] = pdfDoc.getPageNumber(pdfDoc.getLastPage());
                addParagraphs(doc, connection, SQL[i], FIELD[i]);
                doc.add(new AreaBreak());
            }

            pdfDoc.getPage(start[0]).setPageLabel(PageLabelNumberingStyle.UPPERCASE_LETTERS, null);
            pdfDoc.getPage(start[1]).setPageLabel(PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS, null);
            pdfDoc.getPage(start[2]).setPageLabel(PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS, "Movies-", start[2] - start[1] + 1);
            connection.close();
        } catch (IOException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void addParagraphs(Document doc, DatabaseConnection connection, String sql, String field)
            throws SQLException {
        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                doc.add(new Paragraph(rs.getString(field)));
            }
        }
    }

    private void listPageLabels(String src, String dest) {
        try (PrintStream out = new PrintStream(new FileOutputStream(dest));
             PdfReader reader = new PdfReader(src);
             PdfDocument pdfDoc = new PdfDocument(reader)) {
            String[] pageLabels = pdfDoc.getPageLabels();
            for (String textLabel : pageLabels) {
                out.println(textLabel);
            }

            out.flush();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void manipulatePageLabel(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfDictionary root = pdfDoc.getCatalog().getPdfObject();
            PdfDictionary labels = root.getAsDictionary(new PdfName("PageLabels"));
            PdfArray nums = labels.getAsArray(PdfName.Nums);
            for (int i = 0; i < nums.size(); i += 2) {
                int n = nums.getAsNumber(i).intValue();
                if (n == 5) {
                    PdfDictionary pageLabel = nums.getAsDictionary(i + 1);
                    pageLabel.remove(PdfName.St);
                    pageLabel.put(PdfName.P, new PdfString("Film-"));
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
