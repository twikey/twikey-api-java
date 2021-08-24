package com.twikey;

import com.twikey.callback.DocumentCallback;
import com.twikey.modal.Customer;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

public class DocumentGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String ct = System.getenv("CT"); // found @ https://www.twikey.com/r/admin#/c/template

    private Customer customer;

    private TwikeyClient api;

    @Before
    public void createCustomer(){
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

        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testInviteMandateWithoutCustomerDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        JSONObject response = api.document().create(Long.parseLong(ct), null, new HashMap<>());
        assertNotNull("Invite URL",response.getString("url"));
        assertNotNull("Document Reference",response.getString("mndtId"));
    }

    @Test
    public void testInviteMandateCustomerDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        JSONObject response = api.document().create(Long.parseLong(ct), customer, new HashMap<>());
        assertNotNull("Invite URL",response.getString("url"));
        assertNotNull("Document Reference",response.getString("mndtId"));
    }

    @Test
    public void getMandatesAndDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.document().feed(new DocumentCallback() {
            @Override
            public void newDocument(JSONObject newMandate) {
                assertNotNull("New mandate",newMandate);
            }

            @Override
            public void updatedDocument(JSONObject updatedMandate) {
                assertNotNull("Updated mandate",updatedMandate);
            }

            @Override
            public void cancelledDocument(JSONObject cancelledMandate) {
                assertNotNull("Cancelled mandate",cancelledMandate);
            }
        });
    }
}
