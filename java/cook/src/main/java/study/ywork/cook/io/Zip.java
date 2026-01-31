package study.ywork.cook.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class Zip {
    public static void main(String[] args) throws IOException {
        String fileName = "test.jar";
        if (args.length > 0) {
            fileName = args[0];
        }

        Manifest mf = new Manifest();
        Attributes attrs = new Attributes();
        attrs.putValue("Creator", Zip.class.getName());
        mf.getEntries().put("Creater", attrs);
        JarOutputStream jf = new JarOutputStream(new FileOutputStream(fileName), mf);
        ZipEntry zBin = new ZipEntry("foo.bin");
        jf.putNextEntry(zBin);
        byte[] data = { 1, 2, 3, 4 };
        jf.write(data);
        ZipEntry zDir = new ZipEntry("bar/");
        jf.putNextEntry(zDir);
        ZipEntry zText = new ZipEntry("bar/bar.txt");
        jf.putNextEntry(zText);
        jf.write("Hello World\n".getBytes("UTF-8"));
        jf.close();
    }
}
