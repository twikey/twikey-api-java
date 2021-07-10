package com.twikey;

import com.twikey.callback.DocumentCallback;
import com.twikey.modal.Customer;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TwikeyAPITest {

    private String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private long ct = Long.getLong("CT",0L); // found @ https://www.twikey.com/r/admin#/c/template

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

        api = new TwikeyClient(apiKey,true)
            .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testInviteMandateWithoutCustomerDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey);
        System.out.println(api.document().create(ct,null,new HashMap<>()));
    }

    @Test
    public void testInviteMandateCustomerDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey);
        System.out.println(api.document().create(ct,customer,new HashMap<>()));
    }

    @Test
    public void testCreateInvoice() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey);
        Map<String, String> invoiceDetails = new HashMap<>();
        invoiceDetails.put("number", "Invss123");
        invoiceDetails.put("title", "Invoice April");
        invoiceDetails.put("remittance", "123456789123");
        invoiceDetails.put("amount", "10.90");
        invoiceDetails.put("date", "2020-03-20");
        invoiceDetails.put("duedate","2020-04-28");
        System.out.println(api.invoice().create(ct,customer,invoiceDetails));
    }

    @Test
    public void getMandatesAndDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey);
        api.document().feed(new DocumentCallback() {
            @Override
            public void newDocument(JSONObject newMandate) {
                System.out.println("New mandate: "+newMandate);
            }

            @Override
            public void updatedDocument(JSONObject updatedMandate) {
                System.out.println("Updated mandate: "+updatedMandate);
            }

            @Override
            public void cancelledDocument(JSONObject cancelledMandate) {
                System.out.println("Cancelled mandate: "+cancelledMandate);
            }
        });
    }

    @Test
    public void getInvoicesAndDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeNotNull(apiKey);
        api.invoice().feed(updatedInvoice -> System.out.println("Updated invoice: "+updatedInvoice));
    }

    @Test
    public void verifySignatureAndDecryptAccountInfo() {
        // exiturl defined in template http://example.com?mandatenumber={{mandateNumber}}&status={{status}}&signature={{s}}&account={{account}}
        // outcome http://example.com?mandatenumber=MYDOC&status=ok&signature=8C56F94905BBC9E091CB6C4CEF4182F7E87BD94312D1DD16A61BF7C27C18F569&account=2D4727E936B5353CA89B908309686D74863521CAB32D76E8C2BDD338D3D44BBA

        String outcome = "http://example.com?mandatenumber=MYDOC&status=ok&" +
                "signature=8C56F94905BBC9E091CB6C4CEF4182F7E87BD94312D1DD16A61BF7C27C18F569&" +
                "account=2D4727E936B5353CA89B908309686D74863521CAB32D76E8C2BDD338D3D44BBA";

        String websiteKey = "BE04823F732EDB2B7F82252DDAF6DE787D647B43A66AE97B32773F77CCF12765";
        String doc = "MYDOC";
        String status = "ok";

        String signatureInOutcome = "8C56F94905BBC9E091CB6C4CEF4182F7E87BD94312D1DD16A61BF7C27C18F569";
        String encryptedAccountInOutcome = "2D4727E936B5353CA89B908309686D74863521CAB32D76E8C2BDD338D3D44BBA";
        assertTrue("Valid Signature",TwikeyClient.verifyExiturlSignature(websiteKey,doc,status,null,signatureInOutcome));
        String[] ibanAndBic = TwikeyClient.decryptAccountInformation(websiteKey, doc, encryptedAccountInOutcome);
        assertEquals("BE08001166979213",ibanAndBic[0]);
        assertEquals("GEBABEBB",ibanAndBic[1]);
    }
}
