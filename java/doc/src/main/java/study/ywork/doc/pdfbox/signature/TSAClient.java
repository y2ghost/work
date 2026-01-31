package study.ywork.doc.pdfbox.signature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.util.Hex;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class TSAClient {
    private static final Log LOG = LogFactory.getLog(TSAClient.class);
    private static final DigestAlgorithmIdentifierFinder ALGORITHM_OID_FINDER =
            new DefaultDigestAlgorithmIdentifierFinder();

    private final URL url;
    private final String username;
    private final String password;
    private final MessageDigest digest;
    private static final Random RANDOM = new SecureRandom();

    public TSAClient(URL url, String username, String password, MessageDigest digest) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.digest = digest;
    }

    public TimeStampToken getTimeStampToken(InputStream content) throws IOException {
        digest.reset();
        DigestInputStream dis = new DigestInputStream(content, digest);
        while (dis.read() != -1) {
            // NOOP
        }

        byte[] hash = digest.digest();
        int nonce = RANDOM.nextInt(Integer.MAX_VALUE);
        TimeStampRequestGenerator tsaGenerator = new TimeStampRequestGenerator();
        tsaGenerator.setCertReq(true);
        ASN1ObjectIdentifier oid = ALGORITHM_OID_FINDER.find(digest.getAlgorithm()).getAlgorithm();
        TimeStampRequest request = tsaGenerator.generate(oid, hash, BigInteger.valueOf(nonce));

        byte[] encodedRequest = request.getEncoded();
        byte[] tsaResponse = getTSAResponse(encodedRequest);
        TimeStampResponse response = null;

        try {
            response = new TimeStampResponse(tsaResponse);
            response.validate(request);
        } catch (TSPException e) {
            LOG.error("request: " + Hex.getString(encodedRequest));
            if (response != null) {
                LOG.error("response: " + Hex.getString(tsaResponse));
                if ("response contains wrong nonce value.".equals(e.getMessage())) {
                    LOG.error("request nonce: " + request.getNonce().toString(16));
                    if (response.getTimeStampToken() != null) {
                        TimeStampTokenInfo tsi = response.getTimeStampToken().getTimeStampInfo();
                        if (tsi != null && tsi.getNonce() != null) {
                            LOG.error("response nonce: " + tsi.getNonce().toString(16));
                        }
                    }
                }
            }

            throw new IOException(e);
        }

        TimeStampToken timeStampToken = response.getTimeStampToken();
        if (timeStampToken == null) {
            throw new IOException("Response from " + url +
                    " does not have a time stamp token, status: " + response.getStatus() +
                    " (" + response.getStatusString() + ")");
        }

        return timeStampToken;
    }

    private byte[] getTSAResponse(byte[] request) throws IOException {
        LOG.debug("Opening connection to TSA server");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/timestamp-query");
        LOG.debug("Established connection to TSA server");

        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            String contentEncoding = connection.getContentEncoding();
            if (contentEncoding == null) {
                contentEncoding = StandardCharsets.UTF_8.name();
            }

            connection.setRequestProperty("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString(
                            (username + ":" + password).getBytes(contentEncoding)));
        }

        try (OutputStream output = connection.getOutputStream()) {
            output.write(request);
        } catch (IOException ex) {
            LOG.error("Exception when writing to " + this.url, ex);
            throw ex;
        }

        LOG.debug("Waiting for response from TSA server");
        byte[] response;

        try (InputStream input = connection.getInputStream()) {
            response = IOUtils.toByteArray(input);
        } catch (IOException ex) {
            LOG.error("Exception when reading from " + this.url, ex);
            throw ex;
        }

        LOG.debug("Received response from TSA server");
        return response;
    }
}
