package study.ywork.cook.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZip {
    public static enum Mode {
        LIST, EXTRACT;
    }

    protected Mode mode = Mode.LIST;
    protected ZipFile zippy;
    protected byte[] b = new byte[8092];

    public static void main(String[] argv) {
        UnZip u = new UnZip();
        for (int i = 0; i < argv.length; i++) {
            if ("-x".equals(argv[i])) {
                u.setMode(Mode.EXTRACT);
                continue;
            }

            String candidate = argv[i];
            System.err.println("Trying path " + candidate);

            if (candidate.endsWith(".zip") || candidate.endsWith(".jar")) {
                u.unZip(candidate);
            } else {
                System.err.println("Not a zip file? " + candidate);
            }
        }

        System.err.println("All done!");
    }

    protected void setMode(Mode m) {
        mode = m;
    }

    protected SortedSet<String> dirsMade;

    public void unZip(String fileName) {
        dirsMade = new TreeSet<String>();
        try {
            zippy = new ZipFile(fileName);
            @SuppressWarnings("unchecked")
            Enumeration<ZipEntry> all = (Enumeration<ZipEntry>) zippy.entries();
            while (all.hasMoreElements()) {
                getFile((ZipEntry) all.nextElement());
            }
        } catch (IOException err) {
            System.err.println("IO Error: " + err);
            return;
        }
    }

    protected boolean warnedMkDir = false;

    protected void getFile(ZipEntry e) throws IOException {
        String zipName = e.getName();
        switch (mode) {
        case EXTRACT:
            if (zipName.startsWith("/")) {
                if (!warnedMkDir)
                    System.out.println("Ignoring absolute paths");
                warnedMkDir = true;
                zipName = zipName.substring(1);
            }

            if (zipName.endsWith("/")) {
                return;
            }

            int ix = zipName.lastIndexOf('/');
            if (ix > 0) {
                String dirName = zipName.substring(0, ix);
                if (!dirsMade.contains(dirName)) {
                    File d = new File(dirName);
                    if (!(d.exists() && d.isDirectory())) {
                        System.out.println("Creating Directory: " + dirName);
                        if (!d.mkdirs()) {
                            System.err.println("Warning: unable to mkdir " + dirName);
                        }
                        dirsMade.add(dirName);
                    }
                }
            }

            System.err.println("Creating " + zipName);
            FileOutputStream os = new FileOutputStream(zipName);
            InputStream is = zippy.getInputStream(e);
            int n = 0;

            while ((n = is.read(b)) > 0)
                os.write(b, 0, n);
            is.close();
            os.close();
            break;
        case LIST:
            if (e.isDirectory()) {
                System.out.println("Directory " + zipName);
            } else {
                System.out.println("File " + zipName);
            }

            break;
        default:
            throw new IllegalStateException("mode value (" + mode + ") bad");
        }
    }
}
