package study.ywork.doc.itext.form;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.IOException;

public class RadioButtons {
    private static final String DEST = "radioButtons.pdf";
    private static final String[] LANGUAGES = {"English", "German", "French", "Spanish", "Dutch"};

    public static void main(String[] args) {
        new RadioButtons().manipulatePdf();
    }

    protected void manipulatePdf() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
             Document doc = new Document(pdfDoc)) {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            PdfButtonFormField radioGroup = PdfFormField.createRadioGroup(pdfDoc, "language", "");
            radioGroup.setFieldName("language");
            Rectangle rect = new Rectangle(40, 806, 60 - 40, 788 - 806);
            PdfFormField radio;
            for (int page = 1; page <= LANGUAGES.length; page++) {
                pdfDoc.addNewPage();
                radio = PdfFormField.createRadioButton(pdfDoc, rect, radioGroup, LANGUAGES[page - 1]);
                radio.setPage(page);
                doc.showTextAligned(new Paragraph(LANGUAGES[page - 1]).setFont(font).setFontSize(18),
                    70, 790, page, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                radio.getWidgets().get(0).setColor(new DeviceGray(0.8f).getColorValue());
            }
            PdfAcroForm.getAcroForm(pdfDoc, true).addField(radioGroup);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
