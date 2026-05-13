package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class PrintURLs {
    private PrintURLs() {
    }

    public static void main(String[] args) throws IOException {
        PDDocument doc = null;
        try {
            if (args.length != 1) {
                usage();
            } else {
                doc = Loader.loadPDF(new File(args[0]));
                int pageNum = 0;

                for (PDPage page : doc.getPages()) {
                    pageNum++;
                    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                    List<PDAnnotation> annotations = page.getAnnotations();

                    for (int j = 0; j < annotations.size(); j++) {
                        PDAnnotation annot = annotations.get(j);

                        if (getActionURI(annot) != null) {
                            PDRectangle rect = annot.getRectangle();
                            float x = rect.getLowerLeftX();
                            float y = rect.getUpperRightY();
                            float width = rect.getWidth();
                            float height = rect.getHeight();
                            int rotation = page.getRotation();
                            if (rotation == 0) {
                                PDRectangle pageSize = page.getMediaBox();
                                y = pageSize.getHeight() - y;
                            } else {
                                // NOOP
                            }

                            Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);
                            stripper.addRegion("" + j, awtRect);
                        }
                    }

                    stripper.extractRegions(page);
                    for (int j = 0; j < annotations.size(); j++) {
                        PDAnnotation annot = annotations.get(j);
                        PDActionURI uri = getActionURI(annot);
                        if (uri != null) {
                            String urlText = stripper.getTextForRegion("" + j);
                            System.out.println("Page " + pageNum + ":'" + urlText.trim() + "'=" + uri.getURI());
                        }
                    }
                }
            }
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    private static PDActionURI getActionURI(PDAnnotation annot) {
        try {
            Method actionMethod = annot.getClass().getDeclaredMethod("getAction");
            if (actionMethod.getReturnType().equals(PDAction.class)) {
                PDAction action = (PDAction) actionMethod.invoke(annot);
                if (action instanceof PDActionURI actionURI) {
                    return actionURI;
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // NOOP
        }

        return null;
    }

    private static void usage() {
        System.err.println("usage: " + PrintURLs.class.getName() + " <input-file>");
    }
}
