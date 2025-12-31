package com.twikey;

import com.twikey.callback.DocumentCallback;
import com.twikey.modal.DocumentResponse;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.twikey.modal.DocumentRequests.*;
import static org.junit.Assert.assertNotNull;

public class DocumentGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String _ct = System.getenv("CT"); // found @ https://www.twikey.com/r/admin#/c/template

    private Long ct;

    private Customer customer;

    private Account account;

    private TwikeyClient api;

    @Before
    public void createCustomer() {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && _ct != null);
        ct = Long.parseLong(_ct);
        customer = new Customer()
                .setNumber("Java-Sdk-" + System.currentTimeMillis())
                .setEmail("no-reply@example.com")
                .setFirstname("Twikey")
                .setLastname("Support")
                .setStreet("Derbystraat 43")
                .setCity("Gent")
                .setZip("9000")
                .setCountry("BE")
                .setLang("nl")
                .setMobile("32498665995");

        account = new Account("NL46ABNA8910219718", "ABNANL2A");

        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testInviteMandateWithoutCustomerDetails() throws Exception, TwikeyClient.UserException {
        InviteRequest invite = new InviteRequest(ct)
                .setForceCheck(true)
                .setReminderDays(5);
        DocumentResponse.MandateCreationResponse response = api.document().create(invite);
        assertNotNull("Document Reference", response.getMandateNumber());
        assertNotNull("Invite URL", response.getUrl());
        assertNotNull("Invite key", response.getKey());
    }

    @Test
    public void testInviteMandateCustomerDetails() throws Exception, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InviteRequest invite = new InviteRequest(ct, customer)
                .setForceCheck(true)
                .setReminderDays(5);
        DocumentResponse.MandateCreationResponse response = api.document().create(invite);
        assertNotNull("Document Reference", response.getMandateNumber());
        assertNotNull("Invite URL", response.getUrl());
        assertNotNull("Invite key", response.getKey());
    }

    @Test
    public void testSignMandate() throws Exception, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InviteRequest inviteRequest = new InviteRequest(ct, customer, account)
                .setForceCheck(true)
                .setReminderDays(5);
        SignRequest invite = new SignRequest(inviteRequest, SignRequest.SignMethod.IMPORT);
        DocumentResponse.MandateCreationResponse response = api.document().sign(invite);
        assertNotNull("Document Reference", response.getMandateNumber());
    }

    @Test
    public void testAction() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        MandateActionRequest action = new MandateActionRequest(MandateActionRequest.MandateActionType.REMINDER, "CORERECURRENTNL18166")
                .setReminder(1);
        api.document().action(action);
    }

    @Test
    public void testQuery() throws Exception, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        MandateQuery action = MandateQuery
                .fromCustomerNumber("customer123")
                .withIban("BE51561419613262");
        List<DocumentResponse.Document> response = api.document().query(action);
        assertNotNull("Contracts", response);
    }

    @Test
    public void testCancel() throws Exception, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InviteRequest inviteRequest = new InviteRequest(ct, customer, account)
                .setForceCheck(true)
                .setReminderDays(5);
        SignRequest invite = new SignRequest(inviteRequest, SignRequest.SignMethod.IMPORT);
        DocumentResponse.MandateCreationResponse response = api.document().sign(invite);
        assertNotNull("Document Reference", response.getMandateNumber());
        api.document().cancel(response.getMandateNumber(), "hello");
    }

    @Test
    public void testFetch() throws Exception, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        MandateDetailRequest fetch = new MandateDetailRequest("CORERECURRENTNL18166")
                .setForce(true);
        DocumentResponse.Document response = api.document().fetch(fetch);
        assertNotNull("Document Reference", response.getMandateNumber());
    }

    @Test
    public void testUpdateMandate() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        UpdateMandateRequest update = new UpdateMandateRequest("CORERECURRENTNL18166", customer, account);
        api.document().update(update);
    }

    @Test
    public void testCustomerAccess() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        DocumentResponse.CustomerAccessResponse response = api.document().customerAccess("CORERECURRENTNL18166");
        assertNotNull("Document Customer Url", response.getUrl());
    }

    @Test
    public void testRetrievePdf() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        DocumentResponse.PdfResponse retrievedPdf = api.document().retrievePdf("CORERECURRENTNL18247");
        retrievedPdf.save("target/pdf.pdf");
        assertNotNull("Document Reference", retrievedPdf.getFilename());
        assertNotNull("Document Reference", retrievedPdf.getContent());
    }

    @Test
    public void testUploadPdf() throws Exception, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != 0);
        InviteRequest inviteRequest = new InviteRequest(ct, customer, account)
                .setForceCheck(true)
                .setReminderDays(5);
        SignRequest invite = new SignRequest(inviteRequest, SignRequest.SignMethod.PAPER);
        DocumentResponse.MandateCreationResponse response = api.document().sign(invite);
        assertNotNull("Document Reference", response.getMandateNumber());
        UploadPdfRequest pdfRequest = new UploadPdfRequest(response.getMandateNumber(), "target/test-classes/empty.pdf");
        api.document().uploadPdf(pdfRequest);
    }

    @Test
    public void getMandatesAndDetails() throws Exception, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.document().feed(new DocumentCallback() {
            @Override
            public void newDocument(DocumentResponse.Document newMandate, String evt_time) {
                assertNotNull("New mandate", newMandate);
                System.out.printf("Document created   %s @ %s%n", newMandate.getMandateNumber(), evt_time);
            }

            @Override
            public void updatedDocument(DocumentResponse.Document updatedMandate, String updatedDocumentId, String reason, String author, String evt_time) {
                assertNotNull("Updated mandate", updatedMandate);
                System.out.printf("Document updated   %s b/c %s @ %s%n", updatedDocumentId, reason, evt_time);
            }

            @Override
            public void cancelledDocument(String cancelledMandateId, String reason, String author, String evt_time) {
                assertNotNull("Cancelled mandate", cancelledMandateId);
                System.out.printf("Document cancelled %s b/c %s @ %s%n", cancelledMandateId, reason, evt_time);
            }
        });
    }
}
