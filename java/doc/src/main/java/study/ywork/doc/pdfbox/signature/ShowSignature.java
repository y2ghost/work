package study.ywork.doc.pdfbox.signature;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.encryption.SecurityProvider;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.COSFilterInputStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.util.Hex;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;
import study.ywork.doc.pdfbox.signature.cert.CertificateVerificationException;
import study.ywork.doc.pdfbox.signature.cert.CertificateVerifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class ShowSignature {
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private ShowSignature() {
    }

    public static void main(String[] args) throws IOException,
            TSPException,
            CertificateVerificationException,
            GeneralSecurityException {
        Security.addProvider(SecurityProvider.getProvider());
        ShowSignature show = new ShowSignature();
        show.showSignature(args);
    }

    private void showSignature(String[] args) throws IOException,
            GeneralSecurityException,
            TSPException,
            CertificateVerificationException {
        if (args.length != 2) {
            usage();
        } else {
            String password = args[0];
            File infile = new File(args[1]);
            RandomAccessReadBufferedFile raFile = new RandomAccessReadBufferedFile(infile);
            PDFParser parser = new PDFParser(raFile, password);

            try (PDDocument document = parser.parse(false)) {
                for (PDSignature sig : document.getSignatureDictionaries()) {
                    COSDictionary sigDict = sig.getCOSObject();
                    byte[] contents = sig.getContents();

                    try (FileInputStream fis = new FileInputStream(infile);
                         InputStream signedContentAsStream = new COSFilterInputStream(fis, sig.getByteRange())) {
                        System.out.println("Signature found");
                        if (sig.getName() != null) {
                            System.out.println("Name:     " + sig.getName());
                        }

                        if (sig.getSignDate() != null) {
                            System.out.println("Modified: " + sdf.format(sig.getSignDate().getTime()));
                        }

                        String subFilter = sig.getSubFilter();
                        if (subFilter != null) {
                            switch (subFilter) {
                                case "adbe.pkcs7.detached", "ETSI.CAdES.detached":
                                    verifyPKCS7(signedContentAsStream, contents, sig);
                                    break;
                                case "adbe.pkcs7.sha1": {
                                    CertificateFactory factory = CertificateFactory.getInstance("X.509");
                                    ByteArrayInputStream certStream = new ByteArrayInputStream(contents);
                                    Collection<? extends Certificate> certs = factory.generateCertificates(certStream);
                                    System.out.println("certs=" + certs);
                                    MessageDigest md = MessageDigest.getInstance("SHA1");

                                    try (DigestInputStream dis = new DigestInputStream(signedContentAsStream, md)) {
                                        while (dis.read() != -1) {
                                            // NOOP
                                        }
                                    }

                                    byte[] hash = md.digest();
                                    verifyPKCS7(new ByteArrayInputStream(hash), contents, sig);
                                    break;
                                }
                                case "adbe.x509.rsa_sha1": {
                                    COSString certString = (COSString) sigDict.getDictionaryObject(COSName.CERT);
                                    if (certString == null) {
                                        System.err.println("The /Cert certificate string is missing in the signature dictionary");
                                        return;
                                    }

                                    byte[] certData = certString.getBytes();
                                    CertificateFactory factory = CertificateFactory.getInstance("X.509");
                                    ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
                                    Collection<? extends Certificate> certs = factory.generateCertificates(certStream);
                                    System.out.println("certs=" + certs);
                                    X509Certificate cert = (X509Certificate) certs.iterator().next();

                                    try {
                                        if (sig.getSignDate() != null) {
                                            cert.checkValidity(sig.getSignDate().getTime());
                                            System.out.println("Certificate valid at signing time");
                                        } else {
                                            System.err.println("Certificate cannot be verified without signing time");
                                        }
                                    } catch (CertificateExpiredException ex) {
                                        System.err.println("Certificate expired at signing time");
                                    } catch (CertificateNotYetValidException ex) {
                                        System.err.println("Certificate not yet valid at signing time");
                                    }
                                    if (CertificateVerifier.isSelfSigned(cert)) {
                                        System.err.println("Certificate is self-signed, LOL!");
                                    } else {
                                        System.out.println("Certificate is not self-signed");

                                        if (sig.getSignDate() != null) {
                                            Store<X509CertificateHolder> store = new JcaCertStore(certs);
                                            SigUtils.verifyCertificateChain(store, cert, sig.getSignDate().getTime());
                                        }
                                    }
                                    break;
                                }
                                case "ETSI.RFC3161":
                                    verifyETSIdotRFC3161(signedContentAsStream, contents);
                                    break;

                                default:
                                    System.err.println("Unknown certificate type: " + subFilter);
                                    break;
                            }
                        } else {
                            throw new IOException("Missing subfilter for cert dictionary");
                        }

                        int[] byteRange = sig.getByteRange();
                        if (byteRange.length != 4) {
                            System.err.println("Signature byteRange must have 4 items");
                        } else {
                            long fileLen = infile.length();
                            long rangeMax = byteRange[2] + (long) byteRange[3];
                            int contentLen = contents.length * 2 + 2;

                            if (fileLen != rangeMax || byteRange[0] != 0 || byteRange[1] + contentLen != byteRange[2]) {
                                System.out.println("Signature does not cover whole document");
                            } else {
                                System.out.println("Signature covers whole document");
                            }

                            checkContentValueWithFile(infile, byteRange, contents);
                        }
                    }
                }

                analyseDSS(document);
            } catch (CMSException | OperatorCreationException ex) {
                throw new IOException(ex);
            }

            System.out.println("Analyzed: " + args[1]);
        }
    }

    private void checkContentValueWithFile(File file, int[] byteRange, byte[] contents) throws IOException {
        try (RandomAccessReadBufferedFile raf = new RandomAccessReadBufferedFile(file)) {
            raf.seek(byteRange[1]);
            int c = raf.read();

            if (c != '<') {
                System.err.println("'<' expected at offset " + byteRange[1] + ", but got " + (char) c);
            }

            byte[] contentFromFile = new byte[byteRange[2] - byteRange[1] - 2];
            int contentLength = contentFromFile.length;
            int contentBytesRead = raf.read(contentFromFile);

            while (contentBytesRead > -1 && contentBytesRead < contentLength) {
                contentBytesRead += raf.read(contentFromFile,
                        contentBytesRead,
                        contentLength - contentBytesRead);
            }

            byte[] contentAsHex = Hex.getString(contents).getBytes(StandardCharsets.US_ASCII);
            if (contentBytesRead != contentAsHex.length) {
                System.err.println("Raw content length from file is " +
                        contentBytesRead +
                        ", but internal content string in hex has length " +
                        contentAsHex.length);
            }

            for (int i = 0; i < contentBytesRead; ++i) {
                try {
                    if (Integer.parseInt(String.valueOf((char) contentFromFile[i]), 16) !=
                            Integer.parseInt(String.valueOf((char) contentAsHex[i]), 16)) {
                        System.err.println("Possible manipulation at file offset " +
                                (byteRange[1] + i + 1) + " in signature content");
                        break;
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Incorrect hex value");
                    System.err.println("Possible manipulation at file offset " +
                            (byteRange[1] + i + 1) + " in signature content");
                    break;
                }
            }

            c = raf.read();
            if (c != '>') {
                System.err.println("'>' expected at offset " + byteRange[2] + ", but got " + (char) c);
            }
        }
    }

    private void verifyETSIdotRFC3161(InputStream signedContentAsStream, byte[] contents)
            throws CMSException, NoSuchAlgorithmException, IOException, TSPException,
            OperatorCreationException, CertificateVerificationException, CertificateException {
        TimeStampToken timeStampToken = new TimeStampToken(new CMSSignedData(contents));
        TimeStampTokenInfo timeStampInfo = timeStampToken.getTimeStampInfo();
        System.out.println("Time stamp gen time: " + timeStampInfo.getGenTime());

        if (timeStampInfo.getTsa() != null) {
            System.out.println("Time stamp tsa name: " + timeStampInfo.getTsa().getName());
        }

        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream certStream = new ByteArrayInputStream(contents);
        Collection<? extends Certificate> certs = factory.generateCertificates(certStream);
        System.out.println("certs=" + certs);
        String hashAlgorithm = timeStampInfo.getMessageImprintAlgOID().getId();
        MessageDigest md = MessageDigest.getInstance(hashAlgorithm);

        try (DigestInputStream dis = new DigestInputStream(signedContentAsStream, md)) {
            while (dis.read() != -1) {
                // NOOP
            }
        }

        if (Arrays.equals(md.digest(),
                timeStampInfo.getMessageImprintDigest())) {
            System.out.println("ETSI.RFC3161 timestamp signature verified");
        } else {
            System.err.println("ETSI.RFC3161 timestamp signature verification failed");
        }

        X509Certificate certFromTimeStamp = (X509Certificate) certs.iterator().next();
        SigUtils.checkTimeStampCertificateUsage(certFromTimeStamp);
        SigUtils.validateTimestampToken(timeStampToken);
        SigUtils.verifyCertificateChain(timeStampToken.getCertificates(),
                certFromTimeStamp,
                timeStampInfo.getGenTime());
    }

    private void verifyPKCS7(InputStream signedContentAsStream, byte[] contents, PDSignature sig)
            throws CMSException, OperatorCreationException,
            CertificateVerificationException, GeneralSecurityException,
            TSPException, IOException {
        CMSProcessable signedContent = new CMSProcessableInputStream(signedContentAsStream);
        CMSSignedData signedData = new CMSSignedData(signedContent, contents);
        Store<X509CertificateHolder> certificatesStore = signedData.getCertificates();

        if (certificatesStore.getMatches(null).isEmpty()) {
            throw new IOException("No certificates in signature");
        }

        Collection<SignerInformation> signers = signedData.getSignerInfos().getSigners();
        if (signers.isEmpty()) {
            throw new IOException("No signers in signature");
        }

        SignerInformation signerInformation = signers.iterator().next();
        Collection<X509CertificateHolder> matches =
                certificatesStore.getMatches(signerInformation.getSID());

        if (matches.isEmpty()) {
            throw new IOException("Signer '" + signerInformation.getSID().getIssuer() +
                    ", serial# " + signerInformation.getSID().getSerialNumber() +
                    " does not match any certificates");
        }

        X509CertificateHolder certificateHolder = matches.iterator().next();
        X509Certificate certFromSignedData = new JcaX509CertificateConverter().getCertificate(certificateHolder);
        System.out.println("certFromSignedData: " + certFromSignedData);
        SigUtils.checkCertificateUsage(certFromSignedData);
        TimeStampToken timeStampToken = SigUtils.extractTimeStampTokenFromSignerInformation(signerInformation);

        if (timeStampToken != null) {
            SigUtils.validateTimestampToken(timeStampToken);
            X509Certificate certFromTimeStamp = SigUtils.getCertificateFromTimeStampToken(timeStampToken);
            HashSet<X509CertificateHolder> certificateHolderSet = new HashSet<>();
            certificateHolderSet.addAll(certificatesStore.getMatches(null));
            certificateHolderSet.addAll(timeStampToken.getCertificates().getMatches(null));
            SigUtils.verifyCertificateChain(new CollectionStore<>(certificateHolderSet),
                    certFromTimeStamp,
                    timeStampToken.getTimeStampInfo().getGenTime());
            SigUtils.checkTimeStampCertificateUsage(certFromTimeStamp);
            byte[] tsMessageImprintDigest = timeStampToken.getTimeStampInfo().getMessageImprintDigest();
            String hashAlgorithm = timeStampToken.getTimeStampInfo().getMessageImprintAlgOID().getId();
            byte[] sigMessageImprintDigest = MessageDigest.getInstance(hashAlgorithm).digest(signerInformation.getSignature());

            if (Arrays.equals(tsMessageImprintDigest, sigMessageImprintDigest)) {
                System.out.println("timestamp signature verified");
            } else {
                System.err.println("timestamp signature verification failed");
            }
        }

        try {
            if (sig.getSignDate() != null) {
                certFromSignedData.checkValidity(sig.getSignDate().getTime());
                System.out.println("Certificate valid at signing time");
            } else {
                System.err.println("Certificate cannot be verified without signing time");
            }
        } catch (CertificateExpiredException ex) {
            System.err.println("Certificate expired at signing time");
        } catch (CertificateNotYetValidException ex) {
            System.err.println("Certificate not yet valid at signing time");
        }

        if (signerInformation.getSignedAttributes() != null) {
            Attribute signingTime = signerInformation.getSignedAttributes().get(CMSAttributes.signingTime);
            if (signingTime != null) {
                Time timeInstance = Time.getInstance(signingTime.getAttrValues().getObjectAt(0));
                try {
                    certFromSignedData.checkValidity(timeInstance.getDate());
                    System.out.println("Certificate valid at signing time: " + timeInstance.getDate());
                } catch (CertificateExpiredException ex) {
                    System.err.println("Certificate expired at signing time");
                } catch (CertificateNotYetValidException ex) {
                    System.err.println("Certificate not yet valid at signing time");
                }
            }
        }

        if (signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().
                setProvider(SecurityProvider.getProvider()).build(certFromSignedData))) {
            System.out.println("Signature verified");
        } else {
            System.out.println("Signature verification failed");
        }

        if (CertificateVerifier.isSelfSigned(certFromSignedData)) {
            System.err.println("Certificate is self-signed, LOL!");
        } else {
            System.out.println("Certificate is not self-signed");

            if (sig.getSignDate() != null) {
                SigUtils.verifyCertificateChain(certificatesStore, certFromSignedData, sig.getSignDate().getTime());
            } else {
                System.err.println("Certificate cannot be verified without signing time");
            }
        }
    }

    private Set<X509Certificate> getRootCertificates()
            throws GeneralSecurityException, IOException {
        Set<X509Certificate> rootCertificates = new HashSet<>();
        String filename = System.getProperty("java.home") + "/lib/security/cacerts";
        KeyStore keystore;

        try (FileInputStream is = new FileInputStream(filename)) {
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, null);
        }

        PKIXParameters params = new PKIXParameters(keystore);
        for (TrustAnchor trustAnchor : params.getTrustAnchors()) {
            rootCertificates.add(trustAnchor.getTrustedCert());
        }

        try {
            keystore = KeyStore.getInstance("Windows-ROOT");
            keystore.load(null, null);
            params = new PKIXParameters(keystore);
            for (TrustAnchor trustAnchor : params.getTrustAnchors()) {
                rootCertificates.add(trustAnchor.getTrustedCert());
            }
        } catch (InvalidAlgorithmParameterException | KeyStoreException ex) {
            // NOOP
        }

        return rootCertificates;
    }

    private void analyseDSS(PDDocument document) throws IOException {
        PDDocumentCatalog catalog = document.getDocumentCatalog();
        COSBase dssElement = catalog.getCOSObject().getDictionaryObject(COSName.DSS);

        if (dssElement instanceof COSDictionary tempDict) {
            System.out.println("DSS Dictionary: " + tempDict);
            COSBase certsElement = tempDict.getDictionaryObject(COSName.CERTS);

            if (certsElement instanceof COSArray cosArray) {
                printStreamsFromArray(cosArray, "Cert");
            }

            COSBase ocspsElement = tempDict.getDictionaryObject(COSName.OCSPS);
            if (ocspsElement instanceof COSArray cosArray) {
                printStreamsFromArray(cosArray, "Ocsp");
            }

            COSBase crlElement = tempDict.getDictionaryObject(COSName.CRLS);
            if (crlElement instanceof COSArray cosArray) {
                printStreamsFromArray(cosArray, "CRL");
            }
        }
    }

    private void printStreamsFromArray(COSArray elements, String description) throws IOException {
        for (COSBase baseElem : elements) {
            COSObject streamObj = (COSObject) baseElem;
            if (streamObj.getObject() instanceof COSStream cosStream) {
                try (InputStream is = cosStream.createInputStream()) {
                    byte[] streamBytes = IOUtils.toByteArray(is);
                    System.out.println(description + " (" + elements.indexOf(streamObj) + "): "
                            + Hex.getString(streamBytes));
                }
            }
        }
    }

    private static void usage() {
        System.err.println("usage: java " + ShowSignature.class.getName() +
                " <password (usually empty)> <inputfile>");
    }
}
