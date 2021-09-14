package com.twikey;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * Eg. usage or see unittests for more info
 *
 * <pre>
 * String apiKey = "87DA7055C5D18DC5F3FC084F9F208AB335340977"; // found in https://www.twikey.com/r/admin#/c/settings/api
 * long ct = 1420; // found @ https://www.twikey.com/r/admin#/c/template
 * TwikeyAPI api = new TwikeyAPI(apiKey);
 * System.out.println(api.invoice().create(ct,customer,invoiceDetails));
 * System.out.println(api.document().create(ct,null,Map.of()));
 * </pre>
 */
public class TwikeyClient {

    private static final String UTF_8 = "UTF-8";

    public static final String X_RESET = "X-RESET";

    private static final String DEFAULT_USER_HEADER = "twikey/java-v0.1.0";
    private static final String PROD_ENVIRONMENT = "https://api.twikey.com/creditor";
    private static final String TEST_ENVIRONMENT = "https://api.beta.twikey.com/creditor";

    private static final long MAX_SESSION_AGE = 23 * 60 * 60 * 60; // max 1day, but use 23 to be safe
    private static final String SALT_OWN = "own";

    private final String apiKey;
    private String privateKey;

    private String endpoint;
    private long lastLogin;
    private String sessionToken;
    private String userAgent = DEFAULT_USER_HEADER;

    private final DocumentGateway documentGateway;
    private final InvoiceGateway invoiceGateway;
    private final TransactionGateway transactionGateway;
    private final PaylinkGateway paylinkGateway;

    /**
     * @param apikey API key
     */
    public TwikeyClient(String apikey) {
        this.apiKey = apikey;
        endpoint = PROD_ENVIRONMENT;
        documentGateway = new DocumentGateway(this);
        invoiceGateway = new InvoiceGateway(this);
        transactionGateway = new TransactionGateway(this);
        paylinkGateway = new PaylinkGateway(this);
    }

    public TwikeyClient withUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public TwikeyClient withPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public TwikeyClient withCustomEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public TwikeyClient withTestEndpoint() {
        this.endpoint = TEST_ENVIRONMENT;
        return this;
    }

    protected String getSessionToken() throws IOException, UnauthenticatedException {
        if ((System.currentTimeMillis() - lastLogin) > MAX_SESSION_AGE) {
            URL myurl = new URL(endpoint);
            HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", userAgent);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);
            con.setDoInput(true);

            try (DataOutputStream output = new DataOutputStream(con.getOutputStream())) {
                if (privateKey != null) {
                    long otp = generateOtp(SALT_OWN, privateKey);
                    output.writeBytes(String.format("apiToken=%s&otp=%d", apiKey, otp));
                } else {
                    output.writeBytes(String.format("apiToken=%s", apiKey));
                }
                output.flush();
            } catch (GeneralSecurityException e) {
                throw new IOException(e);
            }

            sessionToken = con.getHeaderField("Authorization");
            con.disconnect();

            if (sessionToken != null) {
                lastLogin = System.currentTimeMillis();
            } else {
                lastLogin = 0;
                throw new UnauthenticatedException();
            }
        }
        return sessionToken;
    }

    protected static String getPostDataString(Map<String, String> params) {
        try {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), UTF_8));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), UTF_8));
            }
            return result.toString();
        } catch (UnsupportedEncodingException e) {
            // should not happen on UTF8
            throw new RuntimeException(e);
        }
    }

    public URL getUrl(String path) throws MalformedURLException {
        return new URL(String.format("%s%s", endpoint, path));
    }

    public String getUserAgent() {
        return userAgent;
    }

    public DocumentGateway document() {
        return documentGateway;
    }

    public InvoiceGateway invoice() {
        return invoiceGateway;
    }

    public TransactionGateway transaction() {
        return transactionGateway;
    }

    public PaylinkGateway paylink() {
        return paylinkGateway;
    }

    public static class UserException extends Throwable {
        public UserException(String apiError) {
            super(apiError);
        }
    }

    public static class UnauthenticatedException extends UserException {
        public UnauthenticatedException() {
            super("Not authenticated");
        }
    }

    /**
     * @param signatureHeader request.getHeader("X-SIGNATURE")
     * @param queryString     request.getQueryString()
     * @return true for valid signatures
     */
    public boolean verifyWebHookSignature(String signatureHeader, String queryString) {
        byte[] providedSignature = DatatypeConverter.parseHexBinary(signatureHeader);

        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secret);
            byte[] calculated = mac.doFinal(queryString.getBytes(StandardCharsets.UTF_8));
            boolean equal = true;
            for (int i = 0; i < calculated.length; i++) {
                equal = equal && (providedSignature[i] == calculated[i]);
            }
//            System.out.println("Signature = " + equal);
            return equal;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param websitekey Provided in Settings - Website
     * @param document   Mandatenumber or other
     * @param status     Outcome of the request
     * @param token      If provided in the initial request
     * @param signature  Given in the exit url
     * @return whether or not the signature is valid
     */
    public static boolean verifyExiturlSignature(String websitekey, String document, String status, String token, String signature) {
        byte[] providedSignature = DatatypeConverter.parseHexBinary(signature);

        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(websitekey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secret);
            String payload = document + "/" + status;
            if (token != null) {
                payload += "/" + token;
            }
            byte[] calculated = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            boolean equal = true;
            for (int i = 0; i < calculated.length; i++) {
                equal = equal && (providedSignature[i] == calculated[i]);
            }
            return equal;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param websitekey       Provided in Settings - Website
     * @param document         Mandatenumber or other
     * @param encryptedAccount encrypted account info
     * @return new String[]{iban,bic}
     */
    public static String[] decryptAccountInformation(String websitekey, String document, String encryptedAccount) {
        String key = document + websitekey;
        try {
            byte[] keyBytes = MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(keyBytes));
            byte[] val = cipher.doFinal(DatatypeConverter.parseHexBinary(encryptedAccount));
            return new String(val, StandardCharsets.UTF_8).split("/");
        } catch (Exception e) {
            throw new RuntimeException("Exception decrypting : " + encryptedAccount, e);
        }
    }

    /**
     * For use when enhanced security on the API is required
     */
    private static long generateOtp(String salt, String privateKey) throws GeneralSecurityException {
        if (privateKey == null)
            throw new IllegalArgumentException("Invalid key");

        long counter = (long) Math.floor(System.currentTimeMillis() / 30000);
        byte[] key = parseHexBinary(privateKey);

        if (salt != null) {
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            byte[] key2 = new byte[saltBytes.length + key.length];
            System.arraycopy(saltBytes, 0, key2, 0, saltBytes.length);
            System.arraycopy(key, 0, key2, saltBytes.length, key.length);
            key = key2;
        }

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "SHA256"));

        // get the bytes from the int
        byte[] counterAsBytes = new byte[8];
        for (int i = 7; i >= 0; --i) {
            counterAsBytes[i] = (byte) (counter & 255);
            counter = counter >> 8;
        }

        byte[] hash = mac.doFinal(counterAsBytes);
        int offset = hash[19] & 0xf;
        long v = (hash[offset] & 0x7f) << 24 |
                (hash[offset + 1] & 0xff) << 16 |
                (hash[offset + 2] & 0xff) << 8 |
                (hash[offset + 3] & 0xff);
        // last 8 digits are important
        return v % 100000000;
    }

    public static String formatResetAndSetToUTC(final LocalDateTime resetToDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssX");
        return formatter.format(resetToDate.atZone(ZoneId.of("UTC")));
    }
}
