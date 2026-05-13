package study.ywork.doc.pdfbox.signature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class CreateSignedTimeStamp implements SignatureInterface {
    private static final Log LOG = LogFactory.getLog(CreateSignedTimeStamp.class);
    private final String tsaUrl;

    public CreateSignedTimeStamp(String tsaUrl) {
        this.tsaUrl = tsaUrl;
    }

    public void signDetached(File file) throws IOException {
        signDetached(file, file);
    }

    public void signDetached(File inFile, File outFile) throws IOException {
        if (inFile == null || !inFile.exists()) {
            throw new FileNotFoundException("Document for signing does not exist");
        }

        try (PDDocument doc = Loader.loadPDF(inFile);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            signDetached(doc, fos);
        }
    }

    public void signDetached(PDDocument document, OutputStream output) throws IOException {
        int accessPermissions = SigUtils.getMDPPermission(document);
        if (accessPermissions == 1) {
            throw new IllegalStateException(
                    "No changes to the document are permitted due to DocMDP transform parameters dictionary");
        }

        PDSignature signature = new PDSignature();
        signature.setType(COSName.DOC_TIME_STAMP);
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(COSName.getPDFName("ETSI.RFC3161"));
        document.addSignature(signature, this);
        document.saveIncremental(output);
    }

    @Override
    public byte[] sign(InputStream content) throws IOException {
        ValidationTimeStamp validation;
        try {
            validation = new ValidationTimeStamp(tsaUrl);
            return validation.getTimeStampToken(content);
        } catch (NoSuchAlgorithmException | URISyntaxException e) {
            LOG.error("Hashing-Algorithm not found for TimeStamping", e);
        }

        return new byte[]{};
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            usage();
            System.exit(1);
        }

        String tsaUrl = null;
        if ("-tsa".equals(args[1])) {
            tsaUrl = args[2];
        } else {
            usage();
            System.exit(1);
        }

        CreateSignedTimeStamp signing = new CreateSignedTimeStamp(tsaUrl);
        File inFile = new File(args[0]);
        String name = inFile.getName();
        String substring = name.substring(0, name.lastIndexOf('.'));
        File outFile = new File(inFile.getParent(), substring + "_timestamped.pdf");
        signing.signDetached(inFile, outFile);
    }

    private static void usage() {
        System.err.println("usage: java " + CreateSignedTimeStamp.class.getName() + " "
                + "<pdf_to_sign>\n" + "mandatory options:\n"
                + "  -tsa <url>    sign timestamp using the given TSA server\n");
    }
}
