package study.ywork.doc.pdfbox.pdfa;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.xml.DomXmpParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import study.ywork.doc.pdfbox.pdmodel.CreatePDFA;
import study.ywork.doc.pdfbox.signature.CreateSignature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreatePDFATest {
    private static final String OUT_DIR = "target/test-output";

    @BeforeAll
    static void setUp() {
        new File(OUT_DIR).mkdirs();
    }

    @Test
    void testCreatePDFA() throws Exception {
        String pdfaFilename = OUT_DIR + "/PDFA.pdf";
        String signedPdfaFilename = OUT_DIR + "/PDFA_signed.pdf";
        String keystorePath = "src/test/resources/pdfbox/signature/keystore.p12";
        String message = "The quick brown fox jumps over the lazy dog äöüÄÖÜß @°^²³ {[]}";
        String dir = "src/main/resources/fonts/";
        String fontfile = dir + "LiberationSans-Regular.ttf";
        CreatePDFA.main(new String[]{pdfaFilename, message, fontfile});

        // sign PDF - because we want to make sure that the signed PDF is also PDF/A-1b
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(new FileInputStream(keystorePath), "123456".toCharArray());
        CreateSignature signing = new CreateSignature(keystore, "123456".toCharArray());
        signing.signDetached(new File(pdfaFilename), new File(signedPdfaFilename));

        // Verify that it is PDF/A-1b
        ValidationResult result = PreflightParser.validate(new File(signedPdfaFilename));
        for (ValidationError ve : result.getErrorsList()) {
            System.err.println(ve.getErrorCode() + ": " + ve.getDetails());
        }
        assertTrue(result.isValid(), "PDF file created with CreatePDFA is not valid PDF/A-1b");

        // check the XMP metadata
        try (PDDocument document = Loader.loadPDF(new File(pdfaFilename))) {
            PDDocumentCatalog catalog = document.getDocumentCatalog();
            PDMetadata meta = catalog.getMetadata();
            DomXmpParser xmpParser = new DomXmpParser();
            XMPMetadata metadata = xmpParser.parse(meta.toByteArray());
            DublinCoreSchema dc = metadata.getDublinCoreSchema();
            assertEquals(pdfaFilename, dc.getTitle());
        }

        File signedFile = new File(signedPdfaFilename);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(signedFile)))) {
            String line;
            boolean isIncrementalArea = false;
            Set<String> set = new HashSet<>();
            int linePos = 0;
            while ((line = br.readLine()) != null) {
                ++linePos;
                if (line.equals("%%EOF")) {
                    isIncrementalArea = true;
                    set.clear(); // for cases with several revisions
                }
                if (!isIncrementalArea) {
                    continue;
                }
                if (line.matches("\\d+ 0 obj")) {
                    int pos = line.indexOf(" 0 obj");
                    line = line.substring(0, pos);
                    assertFalse(set.contains(line), "object '" + line
                            + " 0 obj' twice in incremental part of PDF at line " + linePos);
                    set.add(line);
                }
            }
        }
    }
}
