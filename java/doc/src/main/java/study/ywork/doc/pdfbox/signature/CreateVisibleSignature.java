package study.ywork.doc.pdfbox.signature;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessStreamCache.StreamCacheCreateFunction;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.util.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

public class CreateVisibleSignature extends CreateSignatureBase {
    private SignatureOptions signatureOptions;
    private PDVisibleSignDesigner visibleSignDesigner;
    private final PDVisibleSigProperties visibleSignatureProperties = new PDVisibleSigProperties();
    private boolean lateExternalSigning = false;
    private StreamCacheCreateFunction streamCache = IOUtils.createMemoryOnlyStreamCache();
    private PDDocument doc = null;

    public boolean isLateExternalSigning() {
        return lateExternalSigning;
    }

    public void setLateExternalSigning(boolean lateExternalSigning) {
        this.lateExternalSigning = lateExternalSigning;
    }

    public StreamCacheCreateFunction getStreamCacheCreateFunction() {
        return streamCache;
    }

    public void setStreamCacheCreateFunction(StreamCacheCreateFunction streamCache) {
        this.streamCache = streamCache;
    }

    public void setVisibleSignDesigner(String filename, int x, int y, int zoomPercent,
                                       InputStream imageStream, int page)
            throws IOException {
        doc = Loader.loadPDF(new File(filename), streamCache);
        visibleSignDesigner = new PDVisibleSignDesigner(doc, imageStream, page);
        visibleSignDesigner.xAxis(x).yAxis(y).zoom(zoomPercent).adjustForRotation();
    }

    public void setVisibleSignDesigner(int zoomPercent, InputStream imageStream)
            throws IOException {
        visibleSignDesigner = new PDVisibleSignDesigner(imageStream);
        visibleSignDesigner.zoom(zoomPercent);
    }

    public void setVisibleSignatureProperties(String name, String location, String reason, int preferredSize,
                                              int page, boolean visualSignEnabled) {
        visibleSignatureProperties.signerName(name).signerLocation(location).signatureReason(reason).
                preferredSize(preferredSize).page(page).visualSignEnabled(visualSignEnabled).
                setPdVisibleSignature(visibleSignDesigner);
    }

    public void setVisibleSignatureProperties(String name, String location, String reason,
                                              boolean visualSignEnabled) {
        visibleSignatureProperties.signerName(name).signerLocation(location).signatureReason(reason).
                visualSignEnabled(visualSignEnabled).setPdVisibleSignature(visibleSignDesigner);
    }

    public CreateVisibleSignature(KeyStore keystore, char[] pin)
            throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {
        super(keystore, pin);
    }

    public void signPDF(File inputFile, File signedFile, String tsaUrl) throws IOException {
        this.signPDF(inputFile, signedFile, tsaUrl, null);
    }

    public void signPDF(File inputFile, File signedFile, String tsaUrl, String signatureFieldName) throws IOException {
        if (inputFile == null || !inputFile.exists()) {
            throw new IOException("Document for signing does not exist");
        }

        setTsaUrl(tsaUrl);
        if (doc == null) {
            doc = Loader.loadPDF(inputFile, streamCache);
        }

        try (FileOutputStream fos = new FileOutputStream(signedFile)) {
            int accessPermissions = SigUtils.getMDPPermission(doc);
            if (accessPermissions == 1) {
                throw new IllegalStateException("No changes to the document are permitted due to DocMDP transform parameters dictionary");
            }

            PDSignature signature = findExistingSignature(doc, signatureFieldName);
            if (signature == null) {
                signature = new PDSignature();
            }

            if (doc.getVersion() >= 1.5f && accessPermissions == 0) {
                SigUtils.setMDPPermission(doc, signature, 2);
            }

            PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm(null);
            if (acroForm != null && acroForm.getNeedAppearances()) {
                if (acroForm.getFields().isEmpty()) {
                    acroForm.getCOSObject().removeItem(COSName.NEED_APPEARANCES);
                } else {
                    System.out.println("/NeedAppearances is set, signature may be ignored by Adobe Reader");
                }
            }


            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            visibleSignatureProperties.buildSignature();

            signature.setName(visibleSignatureProperties.getSignerName());
            signature.setLocation(visibleSignatureProperties.getSignerLocation());
            signature.setReason(visibleSignatureProperties.getSignatureReason());
            signature.setSignDate(Calendar.getInstance());
            SignatureInterface signatureInterface = isExternalSigning() ? null : this;

            if (visibleSignatureProperties.isVisualSignEnabled()) {
                signatureOptions = new SignatureOptions();
                signatureOptions.setVisualSignature(visibleSignatureProperties.getVisibleSignature());
                signatureOptions.setPage(visibleSignatureProperties.getPage() - 1);
                doc.addSignature(signature, signatureInterface, signatureOptions);
            } else {
                doc.addSignature(signature, signatureInterface);
            }

            if (isExternalSigning()) {
                ExternalSigningSupport externalSigning = doc.saveIncrementalForExternalSigning(fos);
                byte[] cmsSignature = sign(externalSigning.getContent());

                if (isLateExternalSigning()) {
                    externalSigning.setSignature(new byte[0]);
                    int offset = signature.getByteRange()[1] + 1;

                    try (RandomAccessFile raf = new RandomAccessFile(signedFile, "rw")) {
                        raf.seek(offset);
                        raf.write(Hex.getBytes(cmsSignature));
                    }
                } else {
                    externalSigning.setSignature(cmsSignature);
                }
            } else {
                doc.saveIncremental(fos);
            }
        }

        IOUtils.closeQuietly(signatureOptions);
        IOUtils.closeQuietly(doc);
    }

    private PDSignature findExistingSignature(PDDocument doc, String sigFieldName) {
        PDSignature signature = null;
        PDSignatureField signatureField;
        PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm(null);

        if (acroForm != null) {
            signatureField = (PDSignatureField) acroForm.getField(sigFieldName);
            if (signatureField != null) {
                signature = signatureField.getSignature();
                if (signature == null) {
                    signature = new PDSignature();
                    signatureField.getCOSObject().setItem(COSName.V, signature);
                } else {
                    throw new IllegalStateException("The signature field " + sigFieldName + " is already signed.");
                }
            }
        }

        return signature;
    }

    public static void main(String[] args) throws KeyStoreException, CertificateException,
            IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        // keytool -storepass 123456 -storetype PKCS12 -keystore file.p12 -genkey -alias client -keyalg RSA
        if (args.length < 4) {
            usage();
            System.exit(1);
        }

        String tsaUrl = null;
        boolean externalSig = false;

        for (int i = 0; i < args.length; i++) {
            if ("-tsa".equals(args[i])) {
                i++;
                if (i >= args.length) {
                    usage();
                    System.exit(1);
                }
                tsaUrl = args[i];
            }

            if ("-e".equals(args[i])) {
                externalSig = true;
            }
        }

        File ksFile = new File(args[0]);
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        char[] pin = args[1].toCharArray();

        try (InputStream is = new FileInputStream(ksFile)) {
            keystore.load(is, pin);
        }

        File documentFile = new File(args[2]);
        CreateVisibleSignature signing = new CreateVisibleSignature(keystore, pin.clone());
        File signedDocumentFile;
        int page;

        try (InputStream imageStream = new FileInputStream(args[3])) {
            String name = documentFile.getName();
            String substring = name.substring(0, name.lastIndexOf('.'));
            signedDocumentFile = new File(documentFile.getParent(), substring + "_signed.pdf");
            page = 1;
            signing.setVisibleSignDesigner(args[2], 0, 0, -50, imageStream, page);
        }

        signing.setVisibleSignatureProperties("name", "location", "Security", 0, page, true);
        signing.setExternalSigning(externalSig);
        signing.signPDF(documentFile, signedDocumentFile, tsaUrl);
    }

    private static void usage() {
        System.err.println("Usage: java " + CreateVisibleSignature.class.getName()
                + " <pkcs12-keystore-file> <pin> <input-pdf> <sign-image>\n" +
                "options:\n" +
                "  -tsa <url>    sign timestamp using the given TSA server\n" +
                "  -e            sign using external signature creation scenario");
    }
}
