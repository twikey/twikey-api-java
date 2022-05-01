package com.twikey;

import com.twikey.modal.Account;
import com.twikey.modal.Customer;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class RefundGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private TwikeyClient api;

    private Customer customer;

    private Account account;

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

        account = new Account("NL46ABNA8910219718","ABNANL2A");

        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testCreateBeneficiaryAndRefund() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        // Add beneficiary account explicitly (if mandates exist for the customer this is optional)
        JSONObject beneficiaryResponse = api.refund().createBeneficiaryAccount(customer, account);
        assertTrue("Available",beneficiaryResponse.getBoolean("available"));

        JSONObject refundResponse = api.refund().create(customer.getNumber(), Map.of(
                "iban", account.getIban(),
                "message", "Refund faulty item",
                "ref", "My internal reference",
                "amount", "10.99"
        ));
        assertNotNull("Refund id",refundResponse.getString("id"));

        api.refund().feed(refund -> {
            assertEquals("Refund was PAID", "PAID", refund.getString("state"));
            assertNotNull("Refund has ref", refund.getString("ref"));
            assertNotNull("Refund has amount", refund.getBigDecimal("amount"));
        });
    }
}
