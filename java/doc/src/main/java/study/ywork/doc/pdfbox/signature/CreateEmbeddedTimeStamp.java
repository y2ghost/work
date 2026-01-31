package study.ywork.doc.pdfbox.signature;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.util.Hex;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CreateEmbeddedTimeStamp {
    private final String tsaUrl;
    private PDDocument document;
    private PDSignature signature;
    private byte[] changedEncodedSignature;

    public CreateEmbeddedTimeStamp(String tsaUrl) {
        this.tsaUrl = tsaUrl;
    }

    public void embedTimeStamp(File file) throws IOException, URISyntaxException {
        embedTimeStamp(file, file);
    }

    public void embedTimeStamp(File inFile, File outFile) throws IOException, URISyntaxException {
        if (inFile == null || !inFile.exists()) {
            throw new FileNotFoundException("Document for signing does not exist");
        }

        try (PDDocument doc = Loader.loadPDF(inFile)) {
            document = doc;
            processTimeStamping(inFile, outFile);
        }
    }

    private void processTimeStamping(File inFile, File outFile) throws IOException, URISyntaxException {
        try {
            byte[] documentBytes = Files.readAllBytes(inFile.toPath());
            processRelevantSignatures(documentBytes);

            if (changedEncodedSignature == null) {
                throw new IllegalStateException("No signature");
            }

            try (FileOutputStream output = new FileOutputStream(outFile)) {
                embedNewSignatureIntoDocument(documentBytes, output);
            }
        } catch (IOException | NoSuchAlgorithmException | CMSException e) {
            throw new IOException(e);
        }
    }

    private void processRelevantSignatures(byte[] documentBytes)
            throws IOException, CMSException, NoSuchAlgorithmException, URISyntaxException {
        signature = SigUtils.getLastRelevantSignature(document);
        if (signature == null) {
            return;
        }

        byte[] sigBlock = signature.getContents(documentBytes);
        CMSSignedData signedData = new CMSSignedData(sigBlock);
        System.out.println("INFO: Byte Range: " + Arrays.toString(signature.getByteRange()));

        if (tsaUrl != null && !tsaUrl.isEmpty()) {
            ValidationTimeStamp validation = new ValidationTimeStamp(tsaUrl);
            signedData = validation.addSignedTimeStamp(signedData);
        }

        byte[] newEncoded = Hex.getBytes(signedData.getEncoded());
        int maxSize = signature.getByteRange()[2] - signature.getByteRange()[1];
        System.out.println(
                "INFO: New Signature has Size: " + newEncoded.length + " maxSize: " + maxSize);

        if (newEncoded.length > maxSize - 2) {
            throw new IOException(
                    "New Signature is too big for existing Signature-Placeholder. Max Place: "
                            + maxSize);
        } else {
            changedEncodedSignature = newEncoded;
        }
    }

    private void embedNewSignatureIntoDocument(byte[] docBytes, OutputStream output)
            throws IOException {
        int[] byteRange = signature.getByteRange();
        output.write(docBytes, byteRange[0], byteRange[1] + 1);
        output.write(changedEncodedSignature);
        int addingLength = byteRange[2] - byteRange[1] - 2 - changedEncodedSignature.length;
        byte[] zeroes = Hex.getBytes(new byte[(addingLength + 1) / 2]);
        output.write(zeroes);
        output.write(docBytes, byteRange[2] - 1, byteRange[3] + 1);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args.length != 3) {
            usage();
            System.exit(1);
        }

        String tsaUrl = null;
        for (int i = 0; i < args.length; i++) {
            if ("-tsa".equals(args[i])) {
                i++;
                if (i >= args.length) {
                    usage();
                    System.exit(1);
                }
                tsaUrl = args[i];
            }
        }

        File inFile = new File(args[0]);
        System.out.println("Input File: " + args[0]);
        String name = inFile.getName();
        String substring = name.substring(0, name.lastIndexOf('.'));

        File outFile = new File(inFile.getParent(), substring + "_eTs.pdf");
        System.out.println("Output File: " + outFile.getAbsolutePath());

        CreateEmbeddedTimeStamp signing = new CreateEmbeddedTimeStamp(tsaUrl);
        signing.embedTimeStamp(inFile, outFile);
    }

    private static void usage() {
        System.err.println("usage: java " + CreateEmbeddedTimeStamp.class.getName() + " "
                + "<pdf_to_sign>\n" + "mandatory option:\n"
                + "  -tsa <url>    sign timestamp using the given TSA server\n");
    }
}
