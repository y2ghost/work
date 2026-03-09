package study.ywork.doc.pdfbox.pdfa;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import study.ywork.doc.pdfbox.pdmodel.CreatePDFA;
import study.ywork.doc.pdfbox.util.PDFMergerExample;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MergePDFATest {
    private static final String OUT_DIR = "target/test-output";

    @BeforeAll
    static void setUp() {
        new File(OUT_DIR).mkdirs();
    }

    @Test
    void testMergePDFA() throws IOException, TransformerException {
        System.out.println("testMergePDFA");
        String pdfaFilename = OUT_DIR + "/Source_PDFA.pdf";
        String pdfaMergedFilename = OUT_DIR + "/Merged_PDFA.pdf";
        String message = "The quick brown fox jumps over the lazy dog äöüÄÖÜß @°^²³ {[]}";
        String dir = "src/main/resources/fonts/";
        String fontfile = dir + "LiberationSans-Regular.ttf";
        CreatePDFA.main(new String[]{pdfaFilename, message, fontfile});

        List<RandomAccessRead> sources = new ArrayList<>();
        sources.add(new RandomAccessReadBufferedFile(pdfaFilename));
        sources.add(new RandomAccessReadBufferedFile(pdfaFilename));
        InputStream is = new PDFMergerExample().merge(sources);
        try (FileOutputStream os = new FileOutputStream(pdfaMergedFilename)) {
            IOUtils.copy(is, os);
        }
        sources.get(0).close();
        sources.get(1).close();

        // Verify that it is PDF/A-1b
        ValidationResult result = PreflightParser.validate(new File(pdfaMergedFilename));
        for (ValidationResult.ValidationError ve : result.getErrorsList()) {
            System.err.println(ve.getErrorCode() + ": " + ve.getDetails());
        }
        assertTrue(result.isValid(), "PDF file created with CreatePDFA is not valid PDF/A-1b");
    }
}
