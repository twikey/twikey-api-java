package com.twikey;

import com.twikey.modal.DocumentRequests;
import com.twikey.modal.InvoiceRequests;
import com.twikey.modal.InvoiceResponse;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class InvoiceGatewayTest {

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
    public void testCreateInvoice() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        String title = "Inv-%s".formatted("Java-Sdk-" + System.currentTimeMillis());
        InvoiceRequests.CreateInvoiceRequest request = new InvoiceRequests.CreateInvoiceRequest(title, 100.0, LocalDate.now().toString(), LocalDate.now().plusMonths(1).toString(), customer);
        InvoiceResponse.Invoice response = api.invoice().create(request);
        assertNotNull("Invoice Id", response.getId());
        assertNotNull("Invoice Url", response.getUrl());

        InvoiceRequests.UpdateInvoiceRequest updateRequest = new InvoiceRequests.UpdateInvoiceRequest(response.getId())
                .setTitle(title + "-update");
        response = api.invoice().update(updateRequest);
        assertNotNull(title + "-update", response.getTitle());

        InvoiceRequests.InvoiceDetailRequest detailrequest = new InvoiceRequests.InvoiceDetailRequest(response.getId())
                .includeCustomer(true)
                .includeMeta(true)
                .includeLastPayment(true);
        response = api.invoice().details(detailrequest);
        assertNotNull("Invoice Id", response.getId());

        InvoiceRequests.InvoiceActionRequest actionRequest = InvoiceRequests.InvoiceActionRequest.simple(response.getId(), InvoiceRequests.Action.email);
        api.invoice().action(actionRequest);
    }


    @Test(expected = TwikeyClient.UserException.class)
    public void testUBLUpload() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        InvoiceRequests.UblUploadRequest request = new InvoiceRequests.UblUploadRequest("target/test-classes/empty.ubl");
        //invalid ubl
        api.invoice().uploadUbl(request);
    }

    @Test
    public void testBatchCreation() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);
        List<InvoiceRequests.CreateInvoiceRequest> invoices = IntStream.range(0, 5)
                .mapToObj(i -> new InvoiceRequests.CreateInvoiceRequest(
                        "Inv-" + (System.currentTimeMillis() / 1000 + i), // invoice number
                        100.0, // amount
                        LocalDate.now().toString(), // title
                        LocalDate.now().plusMonths(1).toString(), // title
                        customer
                ))
                .toList();
        InvoiceRequests.BulkInvoiceRequest request = new InvoiceRequests.BulkInvoiceRequest(invoices);
        String batchId = api.invoice().createBatch(request);
        assertNotNull(batchId);

        InvoiceResponse.BulkInvoiceDetail response = api.invoice().batchDetails(batchId);
        assertNotNull(response);
    }

    @Test
    public void getInvoicesAndDetails() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.invoice().feed(updatedInvoice -> {
            String newState = "";
            if (Objects.equals(updatedInvoice.getState(), "PAID")) {
                String lastpayment_ = updatedInvoice.getLastpayment();
                if (lastpayment_ != null) {
                    newState = "PAID via %s".formatted(lastpayment_);
                }
            } else {
                newState = "now has state %s".formatted(updatedInvoice.getState());
            }
            assertNotNull("invoice", updatedInvoice);
            System.out.printf("Invoice update with number %s %s euro %s%n", updatedInvoice.getNumber(), updatedInvoice.getAmount(), newState);
        }, "meta");
    }

    @Test
    public void testRetrieveInvoicePdf() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey and CT are set", apiKey != null && ct != null);

        // 1. Create a new invoice (we can optionally attach a PDF)
        String title = "PDF-Test-" + System.currentTimeMillis();
        byte[] pdfBytes = Files.readAllBytes(Paths.get("src/test/resources/empty.pdf"));
        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
        InvoiceRequests.CreateInvoiceRequest request = new InvoiceRequests.CreateInvoiceRequest(
                title,
                100.0,
                LocalDate.now().toString(),
                LocalDate.now().plusMonths(1).toString(),
                customer
        ).setPdf(base64Pdf);

        InvoiceResponse.Invoice createdInvoice = api.invoice().create(request);
        String invoiceId = createdInvoice.getId();
        assertNotNull("Invoice ID should not be null", invoiceId);

        InvoiceRequests.InvoicePdfRequest pdfRequest = new InvoiceRequests.InvoicePdfRequest(invoiceId);
        InvoiceResponse.Pdf pdf = api.invoice().pdf(pdfRequest);

        assertNotNull("PDF object should not be null", pdf);
        assertNotNull("PDF content should not be null", pdf.content());
        assertNotNull("PDF filename should not be null", pdf.filename());

        byte[] retrievedBytes = pdf.content().readAllBytes();
        assertTrue("PDF should not be empty", retrievedBytes.length > 0);

        System.out.printf("Retrieved PDF for invoice %s with filename: %s (%d bytes)%n",
                invoiceId, pdf.filename(), retrievedBytes.length);
    }

    @Test
    public void getInvoicePayments() throws IOException, TwikeyClient.UserException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.invoice().payment(payment -> {
            assertNotNull("payment", payment);
            System.out.printf("Payment event with number %s %s euro %s%n", payment.origin(), payment.amount(), payment.details());
        });
    }
}
