package com.twikey;

import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class TransactionGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String mandateNumber = System.getenv("mndtNumber");

    private TwikeyClient api;

    @Before
    public void prepClient() {
        api = new TwikeyClient(apiKey, true)
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testCreate() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey,mandateNumber);
        Map<String, String> extra = new HashMap<>();
        extra.put("message", "Test Message");
        extra.put("amount", "10.00");
        JSONObject linkResponse = api.transaction().create(mandateNumber, extra);
        System.out.println(linkResponse);
        assertNotNull("Payment URL", linkResponse.getString("url"));
        assertNotNull("Invoice Id", linkResponse.getString("id"));
    }

    @Test
    public void testFeed() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey);
        api.transaction().feed(updatedTransaction -> System.out.println("Updated transaction: " + updatedTransaction));
    }

}
