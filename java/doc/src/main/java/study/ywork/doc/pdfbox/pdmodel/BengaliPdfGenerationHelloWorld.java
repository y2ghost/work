package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class BengaliPdfGenerationHelloWorld {
    private static final int LINE_GAP = 5;
    private static final String LOHIT_BENGALI_TTF = "/pdfbox/ttf/Lohit-Bengali.ttf";
    private static final String TEXT_SOURCE_FILE = "/pdfbox/ttf/bengali-samples.txt";
    private static final int FONT_SIZE = 20;
    private static final int MARGIN = 20;

    private BengaliPdfGenerationHelloWorld() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println(
                    "usage: " + BengaliPdfGenerationHelloWorld.class.getName() + " <output-file> ");
            System.exit(1);
        }

        String filename = args[0];
        System.out.println("The generated pdf filename is: " + filename);

        try (PDDocument doc = new PDDocument()) {
            PDFont font = PDType0Font.load(doc,
                    BengaliPdfGenerationHelloWorld.class.getResourceAsStream(LOHIT_BENGALI_TTF),
                    true);
            PDRectangle rectangle = getPageSize();
            float workablePageWidth = rectangle.getWidth() - 2 * MARGIN;
            float workablePageHeight = rectangle.getHeight() - 2 * MARGIN;

            List<List<String>> pagedTexts = getReAlignedTextBasedOnPageHeight(
                    getReAlignedTextBasedOnPageWidth(getBengaliTextFromFile(), font,
                            workablePageWidth),
                    font, workablePageHeight);

            for (List<String> linesForPage : pagedTexts) {
                PDPage page = new PDPage(getPageSize());
                doc.addPage(page);

                try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                    contents.beginText();
                    contents.setFont(font, FONT_SIZE);
                    contents.newLineAtOffset(rectangle.getLowerLeftX() + MARGIN,
                            rectangle.getUpperRightY() - MARGIN);

                    for (String line : linesForPage) {
                        contents.showText(line);
                        contents.newLineAtOffset(0, -(FONT_SIZE + LINE_GAP));
                    }

                    contents.endText();

                }
            }

            doc.save(filename);
        }
    }

    private static List<List<String>> getReAlignedTextBasedOnPageHeight(List<String> originalLines,
                                                                        PDFont font, float workablePageHeight) {
        final float newLineHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000
                * FONT_SIZE + LINE_GAP;
        List<List<String>> realignedTexts = new ArrayList<>();
        float consumedHeight = 0;
        List<String> linesInAPage = new ArrayList<>();
        for (String line : originalLines) {
            if (newLineHeight + consumedHeight < workablePageHeight) {
                consumedHeight += newLineHeight;
            } else {
                consumedHeight = newLineHeight;
                realignedTexts.add(linesInAPage);
                linesInAPage = new ArrayList<>();
            }

            linesInAPage.add(line);
        }
        realignedTexts.add(linesInAPage);
        return realignedTexts;
    }

    private static List<String> getReAlignedTextBasedOnPageWidth(List<String> originalLines,
                                                                 PDFont font, float workablePageWidth) throws IOException {
        List<String> uniformlyWideTexts = new ArrayList<>();
        float consumedWidth = 0;
        StringBuilder sb = new StringBuilder();
        for (String line : originalLines) {
            float newTokenWidth = 0;
            StringTokenizer st = new StringTokenizer(line, " ", true);
            while (st.hasMoreElements()) {
                String token = st.nextToken();
                newTokenWidth = font.getStringWidth(token) / 1000 * FONT_SIZE;
                if (newTokenWidth + consumedWidth < workablePageWidth) {
                    consumedWidth += newTokenWidth;
                } else {
                    uniformlyWideTexts.add(sb.toString());
                    consumedWidth = newTokenWidth;
                    sb = new StringBuilder();
                }

                sb.append(token);
            }

            uniformlyWideTexts.add(sb.toString());
            consumedWidth = newTokenWidth;
            sb = new StringBuilder();
        }

        return uniformlyWideTexts;
    }

    private static PDRectangle getPageSize() {
        return PDRectangle.A4;
    }

    private static List<String> getBengaliTextFromFile() throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                BengaliPdfGenerationHelloWorld.class.getResourceAsStream(TEXT_SOURCE_FILE), StandardCharsets.UTF_8))) {
            while (true) {
                String line = br.readLine();

                if (line == null) {
                    break;
                }

                if (!line.startsWith("#")) {
                    lines.add(line);
                }
            }
        }

        return lines;
    }
}
