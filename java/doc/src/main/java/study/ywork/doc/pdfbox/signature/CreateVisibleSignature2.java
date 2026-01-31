package study.ywork.doc.pdfbox.signature;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.util.Hex;
import org.apache.pdfbox.util.Matrix;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;

public class CreateVisibleSignature2 extends CreateSignatureBase {
    private SignatureOptions signatureOptions;
    private boolean lateExternalSigning = false;
    private File imageFile = null;

    public CreateVisibleSignature2(KeyStore keystore, char[] pin)
            throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {
        super(keystore, pin);
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public boolean isLateExternalSigning() {
        return lateExternalSigning;
    }

    public void setLateExternalSigning(boolean lateExternalSigning) {
        this.lateExternalSigning = lateExternalSigning;
    }

    public void signPDF(File inputFile, File signedFile, Rectangle2D humanRect, String tsaUrl) throws IOException {
        this.signPDF(inputFile, signedFile, humanRect, tsaUrl, null);
    }

    public void signPDF(File inputFile, File signedFile, Rectangle2D humanRect, String tsaUrl, String signatureFieldName) throws IOException {
        if (inputFile == null || !inputFile.exists()) {
            throw new IOException("Document for signing does not exist");
        }

        setTsaUrl(tsaUrl);
        try (FileOutputStream fos = new FileOutputStream(signedFile);
             PDDocument doc = Loader.loadPDF(inputFile)) {
            int accessPermissions = SigUtils.getMDPPermission(doc);
            if (accessPermissions == 1) {
                throw new IllegalStateException("No changes to the document are permitted due to DocMDP transform parameters dictionary");
            }

            PDSignature signature = null;
            PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm(null);
            PDRectangle rect = null;

            if (acroForm != null) {
                signature = findExistingSignature(acroForm, signatureFieldName);
                if (signature != null) {
                    rect = acroForm.getField(signatureFieldName).getWidgets().get(0).getRectangle();
                }
            }

            if (signature == null) {
                signature = new PDSignature();
            }

            if (rect == null) {
                rect = createSignatureRectangle(doc, humanRect);
            }

            if (doc.getVersion() >= 1.5f && accessPermissions == 0) {
                SigUtils.setMDPPermission(doc, signature, 2);
            }

            if (acroForm != null && acroForm.getNeedAppearances()) {
                if (acroForm.getFields().isEmpty()) {
                    acroForm.getCOSObject().removeItem(COSName.NEED_APPEARANCES);
                } else {
                    System.out.println("/NeedAppearances is set, signature may be ignored by Adobe Reader");
                }
            }

            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setName("Name");
            signature.setLocation("Location");
            signature.setReason("Reason");
            signature.setSignDate(Calendar.getInstance());
            SignatureInterface signatureInterface = isExternalSigning() ? null : this;

            signatureOptions = new SignatureOptions();
            signatureOptions.setVisualSignature(createVisualSignatureTemplate(doc, 0, rect, signature));
            signatureOptions.setPage(0);
            doc.addSignature(signature, signatureInterface, signatureOptions);

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
    }

    private PDRectangle createSignatureRectangle(PDDocument doc, Rectangle2D humanRect) {
        float x = (float) humanRect.getX();
        float y = (float) humanRect.getY();
        float width = (float) humanRect.getWidth();
        float height = (float) humanRect.getHeight();
        PDPage page = doc.getPage(0);
        PDRectangle pageRect = page.getCropBox();
        PDRectangle rect = new PDRectangle();

        switch (page.getRotation()) {
            case 90:
                rect.setLowerLeftY(x);
                rect.setUpperRightY(x + width);
                rect.setLowerLeftX(y);
                rect.setUpperRightX(y + height);
                break;
            case 180:
                rect.setUpperRightX(pageRect.getWidth() - x);
                rect.setLowerLeftX(pageRect.getWidth() - x - width);
                rect.setLowerLeftY(y);
                rect.setUpperRightY(y + height);
                break;
            case 270:
                rect.setLowerLeftY(pageRect.getHeight() - x - width);
                rect.setUpperRightY(pageRect.getHeight() - x);
                rect.setLowerLeftX(pageRect.getWidth() - y - height);
                rect.setUpperRightX(pageRect.getWidth() - y);
                break;
            case 0:
            default:
                rect.setLowerLeftX(x);
                rect.setUpperRightX(x + width);
                rect.setLowerLeftY(pageRect.getHeight() - y - height);
                rect.setUpperRightY(pageRect.getHeight() - y);
                break;
        }

        return rect;
    }

    private InputStream createVisualSignatureTemplate(PDDocument srcDoc, int pageNum,
                                                      PDRectangle rect, PDSignature signature) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(srcDoc.getPage(pageNum).getMediaBox());
            doc.addPage(page);
            PDAcroForm acroForm = new PDAcroForm(doc);
            doc.getDocumentCatalog().setAcroForm(acroForm);
            PDSignatureField signatureField = new PDSignatureField(acroForm);
            PDAnnotationWidget widget = signatureField.getWidgets().get(0);
            List<PDField> acroFormFields = acroForm.getFields();
            acroForm.setSignaturesExist(true);
            acroForm.setAppendOnly(true);
            acroForm.getCOSObject().setDirect(true);
            acroFormFields.add(signatureField);
            widget.setRectangle(rect);
            PDStream stream = new PDStream(doc);
            PDFormXObject form = new PDFormXObject(stream);
            PDResources res = new PDResources();
            form.setResources(res);
            form.setFormType(1);
            PDRectangle bbox = new PDRectangle(rect.getWidth(), rect.getHeight());
            float height = bbox.getHeight();
            Matrix initialScale = null;

            switch (srcDoc.getPage(pageNum).getRotation()) {
                case 90:
                    form.setMatrix(AffineTransform.getQuadrantRotateInstance(1));
                    initialScale = Matrix.getScaleInstance(bbox.getWidth() / bbox.getHeight(), bbox.getHeight() / bbox.getWidth());
                    height = bbox.getWidth();
                    break;
                case 180:
                    form.setMatrix(AffineTransform.getQuadrantRotateInstance(2));
                    break;
                case 270:
                    form.setMatrix(AffineTransform.getQuadrantRotateInstance(3));
                    initialScale = Matrix.getScaleInstance(bbox.getWidth() / bbox.getHeight(), bbox.getHeight() / bbox.getWidth());
                    height = bbox.getWidth();
                    break;
                case 0:
                default:
                    break;
            }

            form.setBBox(bbox);
            PDFont font = new PDType1Font(FontName.HELVETICA_BOLD);
            PDAppearanceDictionary appearance = new PDAppearanceDictionary();
            appearance.getCOSObject().setDirect(true);
            PDAppearanceStream appearanceStream = new PDAppearanceStream(form.getCOSObject());
            appearance.setNormalAppearance(appearanceStream);
            widget.setAppearance(appearance);

            try (PDPageContentStream cs = new PDPageContentStream(doc, appearanceStream)) {
                if (initialScale != null) {
                    cs.transform(initialScale);
                }

                cs.setNonStrokingColor(Color.yellow);
                cs.addRect(-5000, -5000, 10000, 10000);
                cs.fill();

                if (imageFile != null) {
                    cs.saveGraphicsState();
                    cs.transform(Matrix.getScaleInstance(0.25f, 0.25f));
                    PDImageXObject img = PDImageXObject.createFromFileByExtension(imageFile, doc);
                    cs.drawImage(img, 0, 0);
                    cs.restoreGraphicsState();
                }

                float fontSize = 10;
                float leading = fontSize * 1.5f;
                cs.beginText();
                cs.setFont(font, fontSize);
                cs.setNonStrokingColor(Color.black);
                cs.newLineAtOffset(fontSize, height - leading);
                cs.setLeading(leading);
                X509Certificate cert = (X509Certificate) getCertificateChain()[0];

                X500Name x500Name = new X500Name(cert.getSubjectX500Principal().getName());
                RDN cn = x500Name.getRDNs(BCStyle.CN)[0];
                String name = IETFUtils.valueToString(cn.getFirst().getValue());
                String date = signature.getSignDate().getTime().toString();
                String reason = signature.getReason();

                cs.showText("Signer: " + name);
                cs.newLine();
                cs.showText(date);
                cs.newLine();
                cs.showText("Reason: " + reason);
                cs.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }

    private PDSignature findExistingSignature(PDAcroForm acroForm, String sigFieldName) {
        PDSignature signature = null;
        PDSignatureField signatureField;

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
        if (args.length < 3) {
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
        CreateVisibleSignature2 signing = new CreateVisibleSignature2(keystore, pin.clone());

        if (args.length >= 4 && !"-tsa".equals(args[3])) {
            signing.setImageFile(new File(args[3]));
        }

        File signedDocumentFile;
        String name = documentFile.getName();
        String substring = name.substring(0, name.lastIndexOf('.'));
        signedDocumentFile = new File(documentFile.getParent(), substring + "_signed.pdf");
        signing.setExternalSigning(externalSig);
        Rectangle2D humanRect = new Rectangle2D.Float(100, 200, 150, 50);
        signing.signPDF(documentFile, signedDocumentFile, humanRect, tsaUrl, "Signature1");
    }

    private static void usage() {
        System.err.println("Usage: java " + CreateVisibleSignature2.class.getName()
                + " <pkcs12-keystore-file> <pin> <input-pdf> <sign-image>\n" +
                "options:\n" +
                "  -tsa <url>    sign timestamp using the given TSA server\n" +
                "  -e            sign using external signature creation scenario");

        // 生成keystore
        // keytool -storepass 123456 -storetype PKCS12 -keystore file.p12 -genkey -alias client -keyalg RSA
    }
}
