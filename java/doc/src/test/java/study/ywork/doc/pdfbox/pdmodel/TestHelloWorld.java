package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;

@Execution(ExecutionMode.CONCURRENT)
class TestHelloWorld {
    private static final String OUTPUT_DIR = "target/test-output";

    @BeforeAll
    static void setUp() {
        new File(OUTPUT_DIR).mkdirs();
    }

    @Test
    void testHelloWorldTTF() throws IOException {
        String outputFile = OUTPUT_DIR + "/HelloWorldTTF.pdf";
        String message = "HelloWorldTTF.pdf";
        String fontFile = "src/main/resources/fonts/LiberationSans-Regular.ttf";

        new File(outputFile).delete();

        String[] args = new String[]{outputFile, message, fontFile};
        HelloWorldTTF.main(args);

        checkOutputFile(outputFile, message);

        new File(outputFile).delete();
    }

    @Test
    void testHelloWorld() throws IOException {
        String outputDir = "target/test-output";
        String outputFile = outputDir + "/HelloWorld.pdf";
        String message = "HelloWorld.pdf";

        new File(outputFile).delete();

        String[] args = new String[]{outputFile, message};
        HelloWorld.main(args);

        checkOutputFile(outputFile, message);

        new File(outputFile).delete();
    }

    private void checkOutputFile(String outputFile, String message) throws IOException {
        try (PDDocument doc = Loader.loadPDF(new File(outputFile))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String extractedText = stripper.getText(doc).trim();
            Assertions.assertEquals(message, extractedText);
        }
    }
}
