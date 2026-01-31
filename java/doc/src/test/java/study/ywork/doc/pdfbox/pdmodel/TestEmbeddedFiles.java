package study.ywork.doc.pdfbox.pdmodel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class TestEmbeddedFiles {
    @Test
    void testEmbeddedFiles() throws IOException {
        String outputFile = "target/test-output/EmbeddedFile.pdf";
        String embeddedFile = "target/test-output/Test.txt";
        Path outFilePath = Path.of(outputFile);
        Path embeddedFilePath = Path.of(embeddedFile);

        Files.createDirectories(Path.of("target/test-output"));
        Files.deleteIfExists(outFilePath);
        Files.deleteIfExists(embeddedFilePath);
        String[] args = new String[]{outputFile};
        EmbeddedFiles.main(args);
        ExtractEmbeddedFiles.main(args);
        byte[] bytes = Files.readAllBytes(embeddedFilePath);
        String content = new String(bytes, StandardCharsets.US_ASCII);
        Assertions.assertEquals("This is the contents of the embedded file", content);
        Files.deleteIfExists(outFilePath);
        Files.deleteIfExists(embeddedFilePath);
    }
}