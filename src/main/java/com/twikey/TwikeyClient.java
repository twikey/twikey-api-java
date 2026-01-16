package com.twikey;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Eg. usage or see unittests for more info
 *
 * <pre>
 * String apiKey = "87DA7055C5D18DC5F3FC084F9F208AB335340977"; // found in <a href="https://www.twikey.com/r/admin#/c/settings/api">https://www.twikey.com/r/admin#/c/settings/api</a>
 * long ct = 1420; // found @ <a href="https://www.twikey.com/r/admin#/c/template">https://www.twikey.com/r/admin#/c/template</a>
 * TwikeyAPI api = new TwikeyAPI(apiKey);
 * System.out.println(api.invoice().create(ct,customer,invoiceDetails));
 * System.out.println(api.document().create(ct,null,Map.of()));
 * </pre>
 */
public class TwikeyClient {

    private static final String DEFAULT_USER_HEADER = "twikey/java-v0.2.1-SNAPSHOT";
    private static final String PROD_ENVIRONMENT = "https://api.twikey.com/creditor";
    private static final String TEST_ENVIRONMENT = "https://api.beta.twikey.com/creditor";

    public static final String HTTP_FORM_ENCODED = "application/x-www-form-urlencoded";
    public static final String HTTP_APPLICATION_JSON = "application/json";
    public static final String HTTP_APPLICATION_PDF = "application/pdf";

    private static final long MAX_SESSION_AGE = 23 * 60 * 60 * 1000; // max 1day, but use 23 to be safe
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
    private final RefundGateway refundGateway;

    private HttpClient client;

    /**
     * @param apikey API key
     */
    public TwikeyClient(String apikey) {
        this.apiKey = apikey;
        this.endpoint = PROD_ENVIRONMENT;
        this.documentGateway = new DocumentGateway(this);
        this.invoiceGateway = new InvoiceGateway(this);
        this.transactionGateway = new TransactionGateway(this);
        this.paylinkGateway = new PaylinkGateway(this);
        this.refundGateway = new RefundGateway(this);
        this.client = HttpClient.newHttpClient();
    }

    public TwikeyClient withUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public TwikeyClient withHttpClient(HttpClient client) {
        this.client = client;
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
        this.endpoint = "https://javasdk.beta.twikey.com/api/creditor";
        return this;
    }

    protected synchronized String getSessionToken() throws IOException, UnauthenticatedException {
        if ((System.currentTimeMillis() - lastLogin) > MAX_SESSION_AGE) {

            try  {
                String body;
                if (privateKey != null) {
                    long otp = generateOtp(SALT_OWN, privateKey);
                    body = String.format("apiToken=%s&otp=%d", apiKey, otp);
                } else {
                    body = String.format("apiToken=%s", apiKey);
                }

                HttpRequest postRequest = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .header("Content-Type", HTTP_FORM_ENCODED)
                        .header("User-Agent", getUserAgent())
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<Void> response = client.send(postRequest, HttpResponse.BodyHandlers.discarding());
                sessionToken = response.headers().firstValue("Authorization").orElse(null);

                if(sessionToken!= null){
                    lastLogin = System.currentTimeMillis();
                } else {
                    lastLogin = 0;
                    throw new UnauthenticatedException();
                }

            } catch (GeneralSecurityException e) {
                throw new IOException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException(e);
            }
        }
        return sessionToken;
    }

    protected static String getPostDataString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = entry.getValue();
            if (value == null || value.isBlank()) {
                continue;
            }

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), UTF_8));
        }
        return result.toString();
    }

    public URI getUrl(String path) throws MalformedURLException {
        return URI.create(String.format("%s%s", endpoint, path));
    }

    public URI getUrl(String path, String... sideloads) throws MalformedURLException {
        if (sideloads != null && sideloads.length != 0) {
            StringBuilder sb = new StringBuilder(endpoint).append(path);
            sb.append('?');
            for (String sideload : sideloads) {
                sb.append("include=").append(sideload).append('&');
            }
            return URI.create(sb.substring(0, sb.length() - 1));
        }
        else {
            return URI.create(String.format("%s%s", endpoint, path));
        }
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

    public RefundGateway refund() {
        return refundGateway;
    }

    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, UserException {
        try {
            return client.send(request, responseBodyHandler);
        } catch (InterruptedException e) {
            throw new UserException("Interrupted while sending request");
        }
    }

    public static class UserException extends Throwable {
        public UserException(String apiError) {
            super(apiError);
        }

        @Override
        public String toString() {
            return "Twikey user exception " + getMessage();
        }
    }

    public static class UnauthenticatedException extends UserException {
        public UnauthenticatedException() {
            super("Not authenticated");
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * @param signatureHeader request.getHeader("X-SIGNATURE")
     * @param queryString     request.getQueryString()
     * @return true for valid signatures
     */
    public boolean verifyWebHookSignature(String signatureHeader, String queryString) {
        byte[] providedSignature = hexStringToByteArray(signatureHeader);

        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(apiKey.getBytes(UTF_8), "HmacSHA256");
            mac.init(secret);
            byte[] calculated = mac.doFinal(queryString.getBytes(UTF_8));
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
        byte[] providedSignature = hexStringToByteArray(signature);

        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(websitekey.getBytes(UTF_8), "HmacSHA256");
            mac.init(secret);
            String payload = document + "/" + status;
            if (token != null) {
                payload += "/" + token;
            }
            byte[] calculated = mac.doFinal(payload.getBytes(UTF_8));
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
            byte[] keyBytes = MessageDigest.getInstance("MD5").digest(key.getBytes(UTF_8));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(keyBytes));
            byte[] val = cipher.doFinal(hexStringToByteArray(encryptedAccount));
            return new String(val, UTF_8).split("/");
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
        byte[] key = hexStringToByteArray(privateKey);

        if (salt != null) {
            byte[] saltBytes = salt.getBytes(UTF_8);
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
}
