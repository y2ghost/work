package study.ywork.doc.pdfbox.pdmodel;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtractEmbeddedFilesTest {
    @Test
    void testExtractEmbeddedFiles() throws IOException {
        String dir = "target/test-output";
        Files.createDirectories(Path.of(dir));
        String collectionFilename = dir + "/PortableCollection.pdf";
        String attachment1Filename = dir + "/Test1.txt";
        String attachment2Filename = dir + "/Test2.txt";
        String[] args = new String[]{collectionFilename};
        CreatePortableCollection.main(args);
        ExtractEmbeddedFiles.main(args);
        byte[] ba1 = Files.readAllBytes(new File(attachment1Filename).toPath());
        byte[] ba2 = Files.readAllBytes(new File(attachment2Filename).toPath());
        String s1 = new String(ba1, StandardCharsets.US_ASCII);
        String s2 = new String(ba2, StandardCharsets.US_ASCII);
        assertEquals("This is the contents of the first embedded file", s1);
        assertEquals("This is the contents of the second embedded file", s2);
        Files.delete(Paths.get(collectionFilename));
        Files.delete(Paths.get(attachment1Filename));
        Files.delete(Paths.get(attachment2Filename));
    }
}
