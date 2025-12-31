package com.twikey;

import com.twikey.modal.DocumentRequests;
import com.twikey.modal.RefundRequests;
import com.twikey.modal.RefundResponse;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class RefundGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api
    private final String iban = System.getenv("REFUND_IBAN"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String ct = System.getenv("CT");

    private TwikeyClient api;

    private DocumentRequests.Customer customer;

    private DocumentRequests.Account account;

    @Before
    public void createCustomer(){
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

        Assume.assumeTrue("IBAN is set", iban != null);
        account = new DocumentRequests.Account(iban, "ABNANL2A");
        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testCreateBeneficiaryAndRefund() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        RefundRequests.AddBeneficiaryRequest beneficiaryRequest = new RefundRequests.AddBeneficiaryRequest(account.iban())
                .setCustomerNumber(customer.getCustomerNumber());
        // Add beneficiary account explicitly (if mandates exist for the customer this is optional)
        RefundResponse.AddBeneficiaryResponse beneficiaryResponse = api.refund().createBeneficiaryAccount(beneficiaryRequest);
        assertTrue("Available", beneficiaryResponse.getAvailable());

        RefundRequests.NewCreditTransferRequest refundRequest = new RefundRequests.NewCreditTransferRequest(customer.getCustomerNumber(), "hey", 10.0)
                .setIban(beneficiaryResponse.getIban());
        RefundResponse.Refund refundResponse = api.refund().create(refundRequest);
        assertNotNull("Refund id", refundResponse.getId());
    }

    @Test
    public void testDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        RefundResponse.Refund refundResponse = api.refund().details(customer.getCustomerNumber());
        assertNotNull("Refund id", refundResponse.getId());
    }

    @Test
    public void testBatchCreation() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and ct are set", apiKey != null && ct != null);
        RefundRequests.CompleteCreditTransferRequest request = new RefundRequests.CompleteCreditTransferRequest(ct);
        RefundResponse.CreditTransferResponse refundResponse = api.refund().createBatch(request);
        assertNotNull("Refund id", refundResponse);
    }

    @Test
    public void testBatchDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and ct are set", apiKey != null && ct != null);
        RefundRequests.CompleteCreditTransferRequest request = new RefundRequests.CompleteCreditTransferRequest(ct);
        RefundResponse.CreditTransferResponse refundResponse = api.refund().createBatch(request);

        RefundRequests.CompleteCreditTransferDetailsRequest refundRequest = new RefundRequests.CompleteCreditTransferDetailsRequest()
                .setId(String.valueOf(refundResponse.getId()));
        RefundResponse.CreditTransferResponse batchDetailsResponse = api.refund().batchDetails(refundRequest);
        assertNotNull("Refund id", batchDetailsResponse);
    }

    @Test
    public void testRemove() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);

        RefundRequests.AddBeneficiaryRequest beneficiaryRequest = new RefundRequests.AddBeneficiaryRequest(account.iban())
                .setCustomerNumber(customer.getCustomerNumber());
        RefundResponse.AddBeneficiaryResponse beneficiaryResponse = api.refund().createBeneficiaryAccount(beneficiaryRequest);

        RefundRequests.NewCreditTransferRequest refundRequest = new RefundRequests.NewCreditTransferRequest(customer.getCustomerNumber(), "hey", 10.0)
                .setIban(beneficiaryResponse.getIban());
        api.refund().remove(customer.getCustomerNumber());
    }

    @Test
    public void testDisableBeneficiary() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);

        RefundRequests.AddBeneficiaryRequest beneficiaryRequest = new RefundRequests.AddBeneficiaryRequest(account.iban())
                .setCustomerNumber(customer.getCustomerNumber());
        RefundResponse.AddBeneficiaryResponse beneficiaryResponse = api.refund().createBeneficiaryAccount(beneficiaryRequest);

        RefundRequests.DisableBeneficiaryRequest disableBeneficiaryRequest = new RefundRequests.DisableBeneficiaryRequest(account.iban());
        api.refund().disableBeneficiary(disableBeneficiaryRequest);
    }

    @Test
    public void testGetBeneficiaries() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.refund().getBeneficiaries(Boolean.TRUE);
    }

    @Test
    public void testFeed() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.refund().feed(refund -> {
            assertEquals("Refund was PAID", "PAID", refund.getString("state"));
            assertNotNull("Refund has ref", refund.getString("ref"));
            assertNotNull("Refund has amount", refund.getBigDecimal("amount"));
        });
    }
}
