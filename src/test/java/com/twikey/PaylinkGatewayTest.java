package com.twikey;

import com.twikey.modal.Customer;
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

    private Customer customer;

    private TwikeyClient api;

    @Before
    public void createCustomer() {
        customer = new Customer()
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

        api = new TwikeyClient(apiKey, true)
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testCreate() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey, ct);
        Map<String, String> extra = new HashMap<>();
        extra.put("message", "Test Link");
        extra.put("amount", "10.00");
        JSONObject linkResponse = api.paylink().create(Long.parseLong(ct), customer, extra);
        assertNotNull("Payment URL", linkResponse.getString("url"));
        assertNotEquals(0, linkResponse.getLong("id"));

    }

    @Test
    public void testFeed() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey);
        api.paylink().feed(updatedLink -> System.out.println("Updated link: " + updatedLink));
    }
}
