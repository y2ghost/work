package study.ywork.doc.itext.form;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

import java.io.IOException;
import java.util.Map;

public class TextFields {
    private static final String[] RESULT = {
        "textFields.pdf",
        "textFields_filled.pdf"
    };
    private static final String DEST = RESULT[0];
    private static final String TEXT_1 = "text_1";
    private static final String TEXT_2 = "text_2";
    private static final String TEXT_3 = "text_3";

    public static void main(String[] args) {
        new TextFields().manipulatePdf();
    }

    protected void manipulatePdf() {
        createPdf(DEST);
        fillPdf(DEST, RESULT[1]);
    }

    public void createPdf(String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
             Document doc = new Document(pdfDoc)) {
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
            table.setWidth(UnitValue.createPercentValue(80));
            table.addCell(new Cell().add(new Paragraph("Name:")));

            Cell cell = new Cell();
            cell.setNextRenderer(new TextFilledCellRenderer(cell, 1));
            table.addCell(cell);
            table.addCell(new Cell().add(new Paragraph("LoginName:")));

            cell = new Cell();
            cell.setNextRenderer(new TextFilledCellRenderer(cell, 2));
            table.addCell(cell);
            table.addCell(new Cell().add(new Paragraph("Password:")));

            cell = new Cell();
            cell.setNextRenderer(new TextFilledCellRenderer(cell, 3));
            table.addCell(cell);
            table.addCell(new Cell().add(new Paragraph("Reason:")));

            cell = new Cell();
            cell.setNextRenderer(new TextFilledCellRenderer(cell, 4));
            cell.setHeight(60);
            table.addCell(cell);
            doc.add(table);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void fillPdf(String src, String dest) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            Map<String, PdfFormField> fields = form.getFormFields();
            fields.get(TEXT_1).setValue("Bruno haha");
            fields.get(TEXT_2).setFieldFlags(0);
            fields.get(TEXT_2).setBorderColor(ColorConstants.RED);
            fields.get(TEXT_2).setValue("bruno");
            fields.get(TEXT_3).setFieldFlag(PdfFormField.FF_PASSWORD, false);
            fields.get(TEXT_3).getWidgets().get(0).setFlag(PdfAnnotation.PRINT);
            form.getField(TEXT_3).setValue("12345678", "********");
            ((PdfTextFormField) form.getField("text_4")).setMaxLen(12);
            form.getField("text_4").regenerateField();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static class TextFilledCellRenderer extends CellRenderer {
        private final int tf;

        public TextFilledCellRenderer(Cell modelElement, int tf) {
            super(modelElement);
            this.tf = tf;
        }

        @Override
        public IRenderer getNextRenderer() {
            return new TextFilledCellRenderer((Cell) modelElement, tf);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            PdfTextFormField text = PdfFormField.createText(drawContext.getDocument(), getOccupiedAreaBBox(),
                String.format("text_%s", tf), "Enter your name here...");
            text.setBackgroundColor(new DeviceGray(0.75f));
            PdfDictionary borderStyleDict = new PdfDictionary();
            switch (tf) {
                case 1 -> {
                    borderStyleDict.put(PdfName.S, PdfName.B);
                    text.getWidgets().get(0).setBorderStyle(borderStyleDict);
                    text.setFontSize(10);
                    text.setValue("Enter your name here...");
                    text.setRequired(true);
                    text.setJustification(PdfFormField.ALIGN_CENTER);
                }
                case 2 -> {
                    borderStyleDict.put(PdfName.S, PdfName.S);
                    text.getWidgets().get(0).setBorderStyle(borderStyleDict);
                    text.setValue("Name...");
                    text.setMaxLen(8);
                    text.setComb(true);
                    text.setBorderWidth(2);
                }
                case 3 -> {
                    borderStyleDict.put(PdfName.S, PdfName.I);
                    text.getWidgets().get(0).setBorderStyle(borderStyleDict);
                    text.setPassword(true);
                    text.setValue("Choose a password");
                    text.setVisibility(PdfFormField.VISIBLE_BUT_DOES_NOT_PRINT);
                }
                case 4 -> {
                    borderStyleDict.put(PdfName.S, PdfName.D);
                    text.getWidgets().get(0).setBorderStyle(borderStyleDict);
                    text.setBorderColor(ColorConstants.RED);
                    text.setFontSize(8);
                    text.setValue("Enter the reason why you want to win a free " +
                        "accreditation for the Foobar Film Festival");
                    text.setBorderWidth(2);
                    text.setMultiline(true);
                    text.setRequired(true);
                }
                default -> System.err.println("未知错误");
            }

            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(text);
        }
    }
}
