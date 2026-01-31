package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.common.PDPageLabels;

import java.io.IOException;

public class CreatePageLabels {
    private CreatePageLabels() {
    }

    public static void main(String[] args) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            doc.addPage(new PDPage());
            doc.addPage(new PDPage());
            PDPageLabels pageLabels = new PDPageLabels(doc);
            PDPageLabelRange pageLabelRange1 = new PDPageLabelRange();
            pageLabelRange1.setPrefix("RO ");
            pageLabelRange1.setStart(3);
            pageLabelRange1.setStyle(PDPageLabelRange.STYLE_ROMAN_UPPER);
            pageLabels.setLabelItem(0, pageLabelRange1);
            PDPageLabelRange pageLabelRange2 = new PDPageLabelRange();
            pageLabelRange2.setStart(1);
            pageLabelRange2.setStyle(PDPageLabelRange.STYLE_DECIMAL);
            pageLabels.setLabelItem(2, pageLabelRange2);
            doc.getDocumentCatalog().setPageLabels(pageLabels);
            doc.save("labels.pdf");
        }
    }
}
