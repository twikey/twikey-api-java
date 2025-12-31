package com.twikey;

import com.twikey.modal.TransactionRequests;
import com.twikey.modal.TransactionResponse;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class TransactionGatewayTest {

    private final String apiKey = System.getenv("TWIKEY_API_KEY"); // found in https://www.twikey.com/r/admin#/c/settings/api

    private final String mandateNumber = System.getenv("MNDTNUMBER");

    private final String paidTxId = System.getenv("PAIDTXID");

    private TwikeyClient api;

    @Before
    public void prepClient() {
        api = new TwikeyClient(apiKey)
                .withTestEndpoint()
                .withUserAgent("twikey-api-java/junit");
    }

    @Test
    public void testCreate() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeNotNull(apiKey,mandateNumber);
        TransactionRequests.NewTransactionRequest request = new TransactionRequests.NewTransactionRequest(mandateNumber, "hey", 10.0);
        TransactionResponse.Transaction linkResponse = api.transaction().create(request);
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
        assertNotEquals("Transaction Id", 0, linkResponse.getId());
        assertEquals(mandateNumber, linkResponse.getMndtId());
    }

    @Test
    public void testStatus() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeNotNull(apiKey, mandateNumber);
        TransactionRequests.StatusRequest statusRequest = new TransactionRequests.StatusRequest()
                .setMandateNumber(mandateNumber)
                .addInclude(TransactionRequests.StatusRequest.IncludeField.COLLECTION)
                .addInclude(TransactionRequests.StatusRequest.IncludeField.LASTUPDATE);
        TransactionResponse.Transaction linkResponse = api.transaction().status(statusRequest);
        assertNotNull("Transaction", linkResponse);
    }

    @Test
    public void testAction() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeNotNull(apiKey, mandateNumber);
        TransactionRequests.NewTransactionRequest request = new TransactionRequests.NewTransactionRequest(mandateNumber, "hey", 10.0);
        TransactionResponse.Transaction linkResponse = api.transaction().create(request);

        TransactionRequests.ActionRequest actionRequest = new TransactionRequests.ActionRequest(String.valueOf(linkResponse.getId()), TransactionRequests.ActionRequest.Action.REOFFER);
        api.transaction().action(actionRequest);
    }

    @Test
    public void testUpdate() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeNotNull(apiKey, mandateNumber);
        TransactionRequests.NewTransactionRequest request = new TransactionRequests.NewTransactionRequest(mandateNumber, "hey", 10.0);
        TransactionResponse.Transaction linkResponse = api.transaction().create(request);

        TransactionRequests.UpdateTransactionRequest updateRequest = new TransactionRequests.UpdateTransactionRequest(String.valueOf(linkResponse.getId()));
        api.transaction().update(updateRequest);
    }

    @Test
    public void testRefund() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeNotNull(apiKey, mandateNumber, paidTxId);
        TransactionRequests.RefundRequest requestRefund = new TransactionRequests.RefundRequest("6445226", "hey", 10.0);
        TransactionResponse.Refund refundResponse = api.transaction().refund(requestRefund);
        assertNotNull("Transaction Id", refundResponse);
    }

    @Test
    public void testRemove() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeNotNull(apiKey, mandateNumber);
        TransactionRequests.NewTransactionRequest request = new TransactionRequests.NewTransactionRequest(mandateNumber, "hey", 10.0);
        TransactionResponse.Transaction linkResponse = api.transaction().create(request);

        TransactionRequests.RemoveTransactionRequest removeRequest = TransactionRequests.RemoveTransactionRequest.withId(String.valueOf(linkResponse.getId()));
        api.transaction().delete(removeRequest);
    }

    @Test
    public void testQuery() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeNotNull(apiKey, mandateNumber);
        TransactionRequests.QueryRequest queryRequest = new TransactionRequests.QueryRequest(6445226);
        List<TransactionResponse.Transaction> response = api.transaction().query(queryRequest);
        assertNotNull("Transaction", response);
    }

    @Test
    public void testFeed() throws IOException, TwikeyClient.UserException, InterruptedException {
        Assume.assumeTrue("APIKey is set", apiKey != null);
        api.transaction().feed(updatedTransaction -> {
            String state_ = updatedTransaction.getState();
            boolean final_ = updatedTransaction.isFinal();
            String final_msg = "";
            String ref = updatedTransaction.getRef();
            if (ref == null) {
                ref = updatedTransaction.getMsg();
            }
            if (state_.equals("PAID")) {
                state_ = "is now paid";
            } else if (state_.equals("ERROR")) {
                state_ = "failed due to '#%s'".formatted(updatedTransaction.getBkmsg());
                if (final_) {
//                  final means Twikey has gone through all dunning steps, but customer still did not pay
                    final_msg = "with no more dunning steps";
                }
            }
            System.out.printf("Transaction update #%s euro with #%s #%s #%s%n", updatedTransaction.getAmount(), ref, state_, final_msg);
            assertNotNull("Updated transaction", updatedTransaction);
        }, "link");
    }
}
