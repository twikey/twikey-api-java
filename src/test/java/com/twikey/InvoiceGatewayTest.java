package com.twikey;

import com.twikey.modal.Customer;
import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class InvoiceGatewayTest {

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
    public void testCreateInvoice() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey, ct);
        Map<String, String> invoiceDetails = new HashMap<>();
        invoiceDetails.put("number", "Invss123");
        invoiceDetails.put("title", "Invoice April");
        invoiceDetails.put("remittance", "123456789123");
        invoiceDetails.put("amount", "10.90");
        invoiceDetails.put("date", "2020-03-20");
        invoiceDetails.put("duedate", "2020-04-28");
        JSONObject invoiceResponse = api.invoice().create(Long.parseLong(ct), customer, invoiceDetails);
        assertNotNull("Payment URL", invoiceResponse.getString("url"));
        assertNotNull("Invoice Id", invoiceResponse.getString("id"));
    }


    @Test
    public void getInvoicesAndDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey);
        api.invoice().feed(updatedInvoice -> System.out.println("Updated invoice: " + updatedInvoice));
    }

}
