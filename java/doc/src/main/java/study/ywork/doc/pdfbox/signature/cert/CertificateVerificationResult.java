package study.ywork.doc.pdfbox.signature.cert;

import java.security.cert.PKIXCertPathBuilderResult;

public class CertificateVerificationResult {
    private final boolean valid;
    private PKIXCertPathBuilderResult result;
    private Throwable exception;
    
    public CertificateVerificationResult(PKIXCertPathBuilderResult result) {
        this.valid = true;
        this.result = result;
    }

    public CertificateVerificationResult(Throwable exception) {
        this.valid = false;
        this.exception = exception;
    }

    public boolean isValid() {
        return valid;
    }

    public PKIXCertPathBuilderResult getResult() {
        return result;
    }

    public Throwable getException() {
        return exception;
    }
}
