package study.ywork.doc.pdfbox.signature;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

/**
 * keytool -genkeypair -storepass 123456 -storetype pkcs12 -alias test -validity 365 -v -keyalg RSA -keystore keystore.p12
 */
public class CreateSignature extends CreateSignatureBase {
    public CreateSignature(KeyStore keystore, char[] pin)
            throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException {
        super(keystore, pin);
    }

    public void signDetached(File file) throws IOException {
        signDetached(file, file, null);
    }

    public void signDetached(File inFile, File outFile) throws IOException {
        signDetached(inFile, outFile, null);
    }

    public void signDetached(File inFile, File outFile, String tsaUrl) throws IOException {
        if (inFile == null || !inFile.exists()) {
            throw new FileNotFoundException("Document for signing does not exist");
        }

        setTsaUrl(tsaUrl);
        try (FileOutputStream fos = new FileOutputStream(outFile);
             PDDocument doc = Loader.loadPDF(inFile)) {
            signDetached(doc, fos);
        }
    }

    public void signDetached(PDDocument document, OutputStream output)
            throws IOException {
        int accessPermissions = SigUtils.getMDPPermission(document);
        if (accessPermissions == 1) {
            throw new IllegalStateException("No changes to the document are permitted due to DocMDP transform parameters dictionary");
        }

        PDSignature signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setName("Example User");
        signature.setLocation("Los Angeles, CA");
        signature.setReason("Testing");
        signature.setSignDate(Calendar.getInstance());

        if (accessPermissions == 0) {
            SigUtils.setMDPPermission(document, signature, 2);
        }

        if (isExternalSigning()) {
            document.addSignature(signature);
            ExternalSigningSupport externalSigning =
                    document.saveIncrementalForExternalSigning(output);
            byte[] cmsSignature = sign(externalSigning.getContent());
            externalSigning.setSignature(cmsSignature);
        } else {
            SignatureOptions signatureOptions = new SignatureOptions();
            signatureOptions.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE * 2);
            document.addSignature(signature, this, signatureOptions);
            document.saveIncremental(output);
        }
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        if (args.length < 3) {
            usage();
            System.exit(1);
        }

        String tsaUrl = null;
        boolean externalSig = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-tsa")) {
                i++;
                if (i >= args.length) {
                    usage();
                    System.exit(1);
                }
                tsaUrl = args[i];
            }
            if (args[i].equals("-e")) {
                externalSig = true;
            }
        }

        KeyStore keystore = KeyStore.getInstance("PKCS12");
        char[] password = args[1].toCharArray();
        try (InputStream is = new FileInputStream(args[0])) {
            keystore.load(is, password);
        }

        CreateSignature signing = new CreateSignature(keystore, password);
        signing.setExternalSigning(externalSig);

        File inFile = new File(args[2]);
        String name = inFile.getName();
        String substring = name.substring(0, name.lastIndexOf('.'));

        File outFile = new File(inFile.getParent(), substring + "_signed.pdf");
        signing.signDetached(inFile, outFile, tsaUrl);
    }

    private static void usage() {
        System.err.println("usage: java " + CreateSignature.class.getName() + " " +
                "<pkcs12_keystore> <password> <pdf_to_sign>\n" +
                "options:\n" +
                "  -tsa <url>    sign timestamp using the given TSA server\n" +
                "  -e            sign using external signature creation scenario");
    }
}
