package study.ywork.doc.pdfbox.signature.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.util.Hex;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import study.ywork.doc.pdfbox.signature.validation.CertInformationCollector.CertSignatureInformation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class CertInformationHelper {
    private static final Log LOG = LogFactory.getLog(CertInformationHelper.class);

    private CertInformationHelper() {
    }

    protected static String getSha1Hash(byte[] content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return Hex.getString(md.digest(content));
        } catch (NoSuchAlgorithmException e) {
            LOG.error("No SHA-1 Algorithm found", e);
        }

        return null;
    }

    protected static void getAuthorityInfoExtensionValue(byte[] extensionValue,
                                                         CertSignatureInformation certInfo) throws IOException {
        ASN1Sequence asn1Seq = (ASN1Sequence) JcaX509ExtensionUtils.parseExtensionValue(extensionValue);
        Enumeration<?> objects = asn1Seq.getObjects();

        while (objects.hasMoreElements()) {
            ASN1Sequence obj = (ASN1Sequence) objects.nextElement();
            ASN1Encodable oid = obj.getObjectAt(0);
            ASN1TaggedObject location = (ASN1TaggedObject) obj.getObjectAt(1);

            if (X509ObjectIdentifiers.id_ad_ocsp.equals(oid)
                    && location.getTagNo() == GeneralName.uniformResourceIdentifier) {
                ASN1OctetString url = (ASN1OctetString) location.getBaseObject();
                certInfo.setOcspUrl(new String(url.getOctets(), StandardCharsets.UTF_8));
            } else if (X509ObjectIdentifiers.id_ad_caIssuers.equals(oid)) {
                ASN1OctetString uri = (ASN1OctetString) location.getBaseObject();
                certInfo.setIssuerUrl(new String(uri.getOctets(), StandardCharsets.UTF_8));
            }
        }
    }

    protected static String getCrlUrlFromExtensionValue(byte[] extensionValue) throws IOException {
        ASN1Sequence asn1Seq = (ASN1Sequence) JcaX509ExtensionUtils.parseExtensionValue(extensionValue);
        Enumeration<?> objects = asn1Seq.getObjects();

        while (objects.hasMoreElements()) {
            Object obj = objects.nextElement();
            if (obj instanceof ASN1Sequence asn1Sequence) {
                String url = extractCrlUrlFromSequence(asn1Sequence);
                if (url != null) {
                    return url;
                }
            }
        }

        return null;
    }

    private static String extractCrlUrlFromSequence(ASN1Sequence sequence) {
        ASN1TaggedObject taggedObject = (ASN1TaggedObject) sequence.getObjectAt(0);
        taggedObject = (ASN1TaggedObject) taggedObject.getBaseObject();

        if (taggedObject.getBaseObject() instanceof ASN1TaggedObject tempAsn1) {
            taggedObject = tempAsn1;
        } else if (taggedObject.getBaseObject() instanceof ASN1Sequence seq) {
            if (seq.getObjectAt(0) instanceof ASN1TaggedObject tempAsn1) {
                taggedObject = tempAsn1;
            } else {
                return null;
            }
        } else {
            return null;
        }

        if (taggedObject.getBaseObject() instanceof ASN1OctetString uri) {
            String url = new String(uri.getOctets(), StandardCharsets.UTF_8);
            if (url.startsWith("http")) {
                return url;
            }
        }

        return null;
    }
}