package study.ywork.doc.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static String readFileToString(String path) throws IOException {
        byte[] jsBytes = Files.readAllBytes(Path.of(path));
        return new String(jsBytes);
    }

    private FileUtils() {
    }
}
