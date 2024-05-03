package study.ywork.cook.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesInfos {
    public static void main(String[] args) throws IOException {
        final Path p1 = Path.of("lines.txt");
        final Path p2 = Path.of("lines2.txt");
        final Path created1 = Files.createFile(p1);
        final Path created2 = Files.createFile(p2);
        System.out.println(created1);
        System.out.println(created2);
        println("exists", Files.exists(Path.of("/")));
        println("isDirectory", Files.isDirectory(Path.of("/")));
        println("isExecutable", Files.isExecutable(Path.of("/bin/cat")));
        println("isHidden", Files.isHidden(Path.of("~/.profile")));
        println("isReadable", Files.isReadable(Path.of("lines.txt")));
        println("isRegularFile", Files.isRegularFile(Path.of("lines.txt")));
        println("isSameFile", Files.isSameFile(p1, p2));
        println("isSymbolicLink", Files.isSymbolicLink(Path.of("/var")));
        println("isWritable", Files.isWritable(Path.of("/tmp")));
        println("isDirectory", Files.isDirectory(Path.of("/")));
        println("notexists", Files.notExists(Path.of("no_such_file_as_skjfsjljwerjwj")));
        println("probeContentType", Files.probeContentType(Path.of("lines.txt")));
        println("readSymbolicLink", Files.readSymbolicLink(Path.of("/var")));
        println("size", Files.size(Path.of("lines.txt")));
        Files.delete(created1);
        Files.delete(created2);
    }

    private static void println(String s, Object ret) {
        System.out.println(s + " returned " + ret);
    }
}