package com.twikey;

import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TransactionGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String mandateNumber = System.getenv("MNDTNUMBER");

    private TwikeyClient api;

    @Before
    public void prepClient() {
        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testCreate() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey,mandateNumber);
        Map<String, String> extra = new HashMap<>();
        extra.put("message", "Test Message");
        extra.put("amount", "10.00");
        JSONObject linkResponse = api.transaction().create(mandateNumber, extra);
        /*
         * {
         *   "id": 381563,
         *   "contractId": 325638,
         *   "mndtId": "MNDT123",
         *   "contract": "Algemene voorwaarden",
         *   "amount": 10.0,
         *   "msg": "Monthly payment",
         *   "place": null,
         *   "ref": null,
         *   "date": "2017-09-16T14:32:05Z"
         * }
         */
        assertNotEquals("Transaction Id", 0, linkResponse.getLong("id"));
        assertEquals(mandateNumber, linkResponse.getString("mndtId"));
    }

    @Test
    public void testFeed() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.transaction().feed(updatedTransaction -> assertNotNull("Updated transaction", updatedTransaction));
    }
}
