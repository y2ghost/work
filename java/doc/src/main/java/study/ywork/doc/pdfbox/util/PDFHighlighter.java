package study.ywork.doc.pdfbox.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFHighlighter extends PDFTextStripper {
    private Writer highlighterOutput = null;
    private String[] searchedWords;
    private ByteArrayOutputStream textOS = null;
    private Writer textWriter = null;
    private static final Charset ENCODING = StandardCharsets.UTF_16;

    public PDFHighlighter() {
        super.setLineSeparator("");
        super.setWordSeparator("");
        super.setShouldSeparateByBeads(false);
        super.setSuppressDuplicateOverlappingText(false);
    }

    public void generateXMLHighlight(PDDocument pdDocument, String highlightWord, Writer xmlOutput) throws IOException {
        generateXMLHighlight(pdDocument, new String[]{highlightWord}, xmlOutput);
    }

    public void generateXMLHighlight(PDDocument pdDocument, String[] sWords, Writer xmlOutput) throws IOException {
        highlighterOutput = xmlOutput;
        searchedWords = sWords;
        highlighterOutput.write("<XML>\n<Body units=characters " +
                " version=2>\n<Highlight>\n");
        textOS = new ByteArrayOutputStream();
        textWriter = new OutputStreamWriter(textOS, ENCODING);
        writeText(pdDocument, textWriter);
        highlighterOutput.write("</Highlight>\n</Body>\n</XML>");
        highlighterOutput.flush();
    }

    @Override
    protected void endPage(PDPage pdPage) throws IOException {
        textWriter.flush();
        String page = textOS.toString(ENCODING.name());
        textOS.reset();

        if (page.indexOf('a') != -1) {
            page = page.replaceAll("a\\d{1,3}", ".");
        }

        for (String searchedWord : searchedWords) {
            Pattern pattern = Pattern.compile(searchedWord, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(page);

            while (matcher.find()) {
                int begin = matcher.start();
                int end = matcher.end();
                highlighterOutput.write("    <loc " +
                        "pg=" + (getCurrentPageNo() - 1)
                        + " pos=" + begin
                        + " len=" + (end - begin)
                        + ">\n");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        PDFHighlighter xmlExtractor = new PDFHighlighter();
        if (args.length < 2) {
            usage();
        }

        String[] highlightStrings = new String[args.length - 1];
        System.arraycopy(args, 1, highlightStrings, 0, highlightStrings.length);

        try (PDDocument doc = Loader.loadPDF(new File(args[0]))) {
            xmlExtractor.generateXMLHighlight(
                    doc,
                    highlightStrings,
                    new OutputStreamWriter(System.out));
        }
    }

    private static void usage() {
        System.err.println("usage: java " + PDFHighlighter.class.getName() + " <pdf file> word1 word2 word3 ...");
        System.exit(1);
    }
}
