package study.ywork.doc.pdfbox.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PrintImageLocations extends PDFStreamEngine {
    public PrintImageLocations() throws IOException {
        addOperator(new Concatenate(this));
        addOperator(new DrawObject(this));
        addOperator(new SetGraphicsStateParameters(this));
        addOperator(new Save(this));
        addOperator(new Restore(this));
        addOperator(new SetMatrix(this));
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                PrintImageLocations printer = new PrintImageLocations();
                int pageNum = 0;
                for (PDPage page : document.getPages()) {
                    pageNum++;
                    System.out.println("Processing page: " + pageNum);
                    printer.processPage(page);
                }
            }
        }
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String operation = operator.getName();
        if (OperatorName.DRAW_OBJECT.equals(operation)) {
            COSName objectName = (COSName) operands.get(0);
            PDXObject pdxObject = getResources().getXObject(objectName);

            if (pdxObject instanceof PDImageXObject image) {
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();
                System.out.println("*******************************************************************");
                System.out.println("Found image [" + objectName.getName() + "]");

                Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
                float imageXScale = ctmNew.getScalingFactorX();
                float imageYScale = ctmNew.getScalingFactorY();

                System.out.println("position in PDF = " + ctmNew.getTranslateX() + ", " + ctmNew.getTranslateY() + " in user space units");
                System.out.println("raw image size  = " + imageWidth + ", " + imageHeight + " in pixels");
                System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in user space units");
                imageXScale /= 72;
                imageYScale /= 72;
                System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in inches at 72 dpi rendering");
                imageXScale *= 25.4f;
                imageYScale *= 25.4f;
                System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in millimeters at 72 dpi rendering");
                System.out.println();
            } else if (pdxObject instanceof PDFormXObject form) {
                showForm(form);
            }
        } else {
            super.processOperator(operator, operands);
        }
    }

    private static void usage() {
        System.err.println("Usage: java " + PrintImageLocations.class.getName() + " <input-pdf>");
    }

}
