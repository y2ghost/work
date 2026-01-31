package study.ywork.doc.pdfbox.pdmodel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class ExtractEmbeddedFiles {
    private ExtractEmbeddedFiles() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
            System.exit(1);
        }

        File pdfFile = new File(args[0]);
        String filePath = pdfFile.getParent() + FileSystems.getDefault().getSeparator();
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDDocumentNameDictionary namesDictionary =
                    new PDDocumentNameDictionary(document.getDocumentCatalog());
            PDEmbeddedFilesNameTreeNode efTree = namesDictionary.getEmbeddedFiles();
            if (efTree != null) {
                extractFilesFromEFTree(efTree, filePath);
            }

            for (PDPage page : document.getPages()) {
                extractFilesFromPage(page, filePath);
            }
        }
    }

    private static void extractFilesFromPage(PDPage page, String filePath) throws IOException {
        for (PDAnnotation annotation : page.getAnnotations()) {
            if (annotation instanceof PDAnnotationFileAttachment annotationFileAttachment) {
                PDFileSpecification fileSpec = annotationFileAttachment.getFile();
                if (fileSpec instanceof PDComplexFileSpecification complexFileSpec) {
                    PDEmbeddedFile embeddedFile = getEmbeddedFile(complexFileSpec);
                    if (embeddedFile != null) {
                        extractFile(filePath, complexFileSpec.getFilename(), embeddedFile);
                    }
                }
            }
        }
    }

    private static void extractFilesFromEFTree(PDNameTreeNode<PDComplexFileSpecification> efTree, String filePath) throws IOException {
        Map<String, PDComplexFileSpecification> names = efTree.getNames();
        if (names != null) {
            extractFiles(names, filePath);
        } else {
            List<PDNameTreeNode<PDComplexFileSpecification>> kids = efTree.getKids();
            if (kids == null) {
                return;
            }
            for (PDNameTreeNode<PDComplexFileSpecification> node : kids) {
                extractFilesFromEFTree(node, filePath);
            }
        }
    }

    private static void extractFiles(Map<String, PDComplexFileSpecification> names, String filePath)
            throws IOException {
        for (Entry<String, PDComplexFileSpecification> entry : names.entrySet()) {
            PDComplexFileSpecification fileSpec = entry.getValue();
            PDEmbeddedFile embeddedFile = getEmbeddedFile(fileSpec);
            if (embeddedFile != null) {
                extractFile(filePath, fileSpec.getFilename(), embeddedFile);
            }
        }
    }

    private static void extractFile(String filePath, String filename, PDEmbeddedFile embeddedFile)
            throws IOException {
        String embeddedFilename = filePath + filename;
        File file = new File(embeddedFilename);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            // 某些目录包含目录，则需要创建
            System.out.println("Creating " + parentDir);
            parentDir.mkdirs();
        }
        System.out.println("Writing " + embeddedFilename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(embeddedFile.toByteArray());
        }
    }

    private static PDEmbeddedFile getEmbeddedFile(PDComplexFileSpecification fileSpec) {
        PDEmbeddedFile embeddedFile = null;
        if (fileSpec != null) {
            embeddedFile = fileSpec.getEmbeddedFileUnicode();
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileDos();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileMac();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileUnix();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFile();
            }
        }
        return embeddedFile;
    }

    private static void usage() {
        System.err.println("Usage: java " + ExtractEmbeddedFiles.class.getName() + " <input-pdf>");
    }
}
