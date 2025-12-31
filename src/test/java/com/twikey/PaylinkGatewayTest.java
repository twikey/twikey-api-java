package com.twikey;

import com.twikey.modal.DocumentRequests;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class PaylinkGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String ct = System.getenv("CT"); // found @ https://www.twikey.com/r/admin#/c/template

    private DocumentRequests.Customer customer;

    private TwikeyClient api;

    @Before
    public void createCustomer() {
        customer = new DocumentRequests.Customer()
                .setNumber("customerNum123")
                .setEmail("no-reply@example.com")
                .setFirstname("Twikey")
                .setLastname("Support")
                .setStreet("Derbystraat 43")
                .setCity("Gent")
                .setZip("9000")
                .setCountry("BE")
                .setLang("nl")
                .setMobile("32498665995");

        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testCreate() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        Map<String, String> extra = new HashMap<>();
        extra.put("message", "Test Link");
        extra.put("amount", "10.00");
        JSONObject linkResponse = api.paylink().create(Long.parseLong(ct), customer, extra);
        /*
         * {
         *   "id": 1,
         *   "amount": 55.66,
         *   "msg": "Test",
         *   "url": "https://mycompany.twikey.com/payment/tr_l2iKz0LT8HvRrmf0"
         * }
         */
        assertNotEquals(0, linkResponse.getLong("id"));
        assertNotNull("Payment URL", linkResponse.getString("url"));

    }

    @Test
    public void testFeed() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.paylink().feed(updatedLink -> assertNotNull("Updated link", updatedLink),"meta");
        api.paylink().feed(updatedLink -> assertNotNull("Updated link", updatedLink));
    }
}
