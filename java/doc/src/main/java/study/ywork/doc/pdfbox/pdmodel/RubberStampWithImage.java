package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationRubberStamp;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RubberStampWithImage {
    private static final String SAVE_GRAPHICS_STATE = "q\n";
    private static final String RESTORE_GRAPHICS_STATE = "Q\n";
    private static final String CONCATENATE_MATRIX = "cm\n";
    private static final String XOBJECT_DO = "Do\n";
    private static final String SPACE = " ";
    private static final NumberFormat FORMATDECIMAL = NumberFormat.getNumberInstance(Locale.US);

    public void doIt(String[] args) throws IOException {
        if (args.length != 3) {
            usage();
        } else {
            try (PDDocument document = Loader.loadPDF(new File(args[0]))) {
                if (document.isEncrypted()) {
                    throw new IOException("Encrypted documents are not supported for this example");
                }

                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    PDPage page = document.getPage(i);
                    List<PDAnnotation> annotations = page.getAnnotations();
                    PDAnnotationRubberStamp rubberStamp = new PDAnnotationRubberStamp();
                    rubberStamp.setName(PDAnnotationRubberStamp.NAME_TOP_SECRET);
                    rubberStamp.setRectangle(new PDRectangle(200, 100));
                    rubberStamp.setContents("A top secret note");
                    PDImageXObject ximage = PDImageXObject.createFromFile(args[2], document);

                    float lowerLeftX = 250;
                    float lowerLeftY = 550;
                    float formWidth = 150;
                    float formHeight = 25;
                    float imgWidth = 50;
                    float imgHeight = 25;

                    PDRectangle rect = new PDRectangle();
                    rect.setLowerLeftX(lowerLeftX);
                    rect.setLowerLeftY(lowerLeftY);
                    rect.setUpperRightX(lowerLeftX + formWidth);
                    rect.setUpperRightY(lowerLeftY + formHeight);

                    PDFormXObject form = new PDFormXObject(document);
                    form.setResources(new PDResources());
                    form.setBBox(rect);
                    form.setFormType(1);

                    try (OutputStream os = form.getStream().createOutputStream()) {
                        drawXObject(ximage, form.getResources(), os, lowerLeftX, lowerLeftY, imgWidth, imgHeight);
                    }

                    PDAppearanceStream myDic = new PDAppearanceStream(form.getCOSObject());
                    PDAppearanceDictionary appearance = new PDAppearanceDictionary(new COSDictionary());
                    appearance.setNormalAppearance(myDic);
                    rubberStamp.setAppearance(appearance);
                    rubberStamp.setRectangle(rect);
                    annotations.add(rubberStamp);
                }

                document.save(args[1]);
            }
        }
    }

    private void drawXObject(PDImageXObject xobject, PDResources resources, OutputStream os,
                             float x, float y, float width, float height) throws IOException {
        COSName xObjectId = resources.add(xobject);
        appendRawCommands(os, SAVE_GRAPHICS_STATE);
        appendRawCommands(os, FORMATDECIMAL.format(width));
        appendRawCommands(os, SPACE);
        appendRawCommands(os, FORMATDECIMAL.format(0));
        appendRawCommands(os, SPACE);
        appendRawCommands(os, FORMATDECIMAL.format(0));
        appendRawCommands(os, SPACE);
        appendRawCommands(os, FORMATDECIMAL.format(height));
        appendRawCommands(os, SPACE);
        appendRawCommands(os, FORMATDECIMAL.format(x));
        appendRawCommands(os, SPACE);
        appendRawCommands(os, FORMATDECIMAL.format(y));
        appendRawCommands(os, SPACE);
        appendRawCommands(os, CONCATENATE_MATRIX);
        appendRawCommands(os, SPACE);
        appendRawCommands(os, "/");
        appendRawCommands(os, xObjectId.getName());
        appendRawCommands(os, SPACE);
        appendRawCommands(os, XOBJECT_DO);
        appendRawCommands(os, SPACE);
        appendRawCommands(os, RESTORE_GRAPHICS_STATE);
    }

    private void appendRawCommands(OutputStream os, String commands) throws IOException {
        os.write(commands.getBytes(StandardCharsets.ISO_8859_1));
    }

    public static void main(String[] args) throws IOException {
        RubberStampWithImage rubberStamp = new RubberStampWithImage();
        rubberStamp.doIt(args);
    }

    private void usage() {
        System.err.println("Usage: java " + getClass().getName() + " <input-pdf> <output-pdf> <image-filename>");
    }
}
