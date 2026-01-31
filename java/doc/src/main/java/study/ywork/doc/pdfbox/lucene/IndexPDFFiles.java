package study.ywork.doc.pdfbox.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public final class IndexPDFFiles {
    private IndexPDFFiles() {
    }

    /**
     * 索引目录下的所有文本文件
     */
    public static void main(String[] args) {
        String usage = "java org.apache.pdfbox.lucene.IndexPDFFiles"
                + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                + "This indexes all PDF documents in DOCS_PATH, creating a Lucene index"
                + "in INDEX_PATH that can be searched with SearchFiles";
        String indexPath = "index";
        String docsPath = null;
        boolean create = true;

        for (int i = 0; i < args.length; i++) {
            if ("-index".equals(args[i]) && i + 1 < args.length) {
                indexPath = args[i + 1];
                i++;
            } else if ("-docs".equals(args[i]) && i + 1 < args.length) {
                docsPath = args[i + 1];
                i++;
            } else if ("-update".equals(args[i])) {
                create = false;
            }
        }

        if (docsPath == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        final File docDir = new File(docsPath);
        if (!docDir.exists() || !docDir.canRead()) {
            System.out.println("Document directory '" + docDir.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");
            try (Directory dir = FSDirectory.open(new File(indexPath).toPath())) {
                Analyzer analyzer = new StandardAnalyzer();
                IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
                if (create) {
                    // 创建新索引文件，会删除已有的索引文件
                    iwc.setOpenMode(OpenMode.CREATE);
                } else {
                    iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
                }
                // 建议配置JVM的内存大小优化性能，例如-Xmx1g
                try (final IndexWriter writer = new IndexWriter(dir, iwc)) {
                    indexDocs(writer, docDir);
                    // 优化搜索性能可以考虑调用writer.forceMerge(1);
                }
            }

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
    }

    static void indexDocs(IndexWriter writer, File file) throws IOException {
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    for (String fileName : files) {
                        indexDocs(writer, new File(file, fileName));
                    }
                }
            } else {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
                    return;
                }

                try {

                    String path = file.getName().toUpperCase();
                    Document doc;
                    if (path.toLowerCase().endsWith(".pdf")) {
                        System.out.println("Indexing PDF document: " + file);
                        doc = LucenePDFDocument.getDocument(file);
                    } else {
                        System.out.println("Skipping " + file);
                        return;
                    }

                    if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                        System.out.println("adding " + file);
                        writer.addDocument(doc);
                    } else {
                        System.out.println("updating " + file);
                        writer.updateDocument(new Term("uid", LucenePDFDocument.createUID(file)), doc);
                    }
                } finally {
                    fis.close();
                }
            }
        }
    }
}
