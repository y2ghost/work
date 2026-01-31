package study.ywork.doc.itext.form;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;

public class Subscribe {
    private static final String DEST = "subscribe2.pdf";
    private static final String SRC = "subscribe.pdf";

    public static void main(String[] args) {
        Subscribe subscribe = new Subscribe();
        subscribe.createPdf(SRC);
        subscribe.manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) {
        createPdf(SRC);
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            form.removeField("personal.password");
            form.getField("personal.name").setValue("test");
            form.getField("personal.loginname").setValue("test");
            form.renameField("personal.reason", "motivation");
            form.getField("personal.loginname").setReadOnly(true);
            form.partialFormFlattening("personal.name");
            form.flattenFields();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    protected void createPdf(String filename) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
             Document doc = new Document(pdfDoc)) {
            PdfFormField personal = PdfFormField.createEmptyField(pdfDoc);
            personal.setFieldName("personal");
            Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
            table.addCell(new Cell().add(new Paragraph("Your name:")));
            Cell cell = new Cell(1, 2);
            PdfTextFormField field = PdfFormField.createText(pdfDoc, new Rectangle(0, 0), "name", "");
            field.setBorderWidth(0);
            personal.addKid(field);
            cell.setNextRenderer(new ChildFieldEvent(field, 1, cell));
            table.addCell(cell);
            table.addCell(new Cell().add(new Paragraph("Login:")));
            cell = new Cell();
            field = PdfFormField.createText(pdfDoc, new Rectangle(0, 0), "loginname", "");
            field.setBorderWidth(0);
            personal.addKid(field);
            cell.setNextRenderer(new ChildFieldEvent(field, 1, cell));
            table.addCell(cell);
            cell = new Cell();
            field = PdfFormField.createText(pdfDoc, new Rectangle(0, 0), "password", "");
            field.setBorderWidth(0);
            field.setPassword(true);
            personal.addKid(field);
            cell.setNextRenderer(new ChildFieldEvent(field, 1, cell));
            table.addCell(cell);
            table.addCell(new Cell().add(new Paragraph("Your motivation:")));
            cell = new Cell(1, 2);
            cell.setHeight(60);
            field = PdfFormField.createText(pdfDoc, new Rectangle(0, 0), "reason", "");
            field.setBorderWidth(0);
            field.setMultiline(true);
            personal.addKid(field);
            cell.setNextRenderer(new ChildFieldEvent(field, 1, cell));
            table.addCell(cell);
            doc.add(table);
            PdfAcroForm.getAcroForm(pdfDoc, true).addField(personal);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
