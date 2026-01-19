package com.twikey;

import com.twikey.callback.TransactionCallback;
import com.twikey.modal.TransactionRequests;
import com.twikey.modal.TransactionResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.twikey.TwikeyClient.HTTP_FORM_ENCODED;
import static com.twikey.TwikeyClient.apiError;
import static com.twikey.TwikeyClient.getPostDataString;

public class TransactionGateway {

    private final TwikeyClient twikeyClient;

    protected TransactionGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * See <a href="https://www.twikey.com/api/#new-transaction">API Documentation</a>
     *
     * <p>Create a new transaction via a POST request to the Twikey API.</p>
     *
     * <p>This method sends the provided request payload to the corresponding endpoint
     * and parses the JSON response into a response model. Typically used to initiate
     * actions such as inviting a customer, creating a mandate, or generating a payment link.</p>
     *
     * <p>Raises an error if the API response contains an error code or if the request fails.</p>
     *
     * @param newTransactionRequest an object representing the payload to send
     * @return a structured {@link TransactionResponse.Transaction} object representing the server’s reply
     * @throws IOException if an I/O error occurs during the request
     * @throws TwikeyClient.UserException if the API returns an error or validation fails
     */
    public TransactionResponse.Transaction create(TransactionRequests.NewTransactionRequest newTransactionRequest) throws IOException, TwikeyClient.UserException {
        Map<String, String> tx = newTransactionRequest.toRequestMap();

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transaction"))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(tx)))
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return TransactionResponse.Transaction.fromJson(json.getJSONArray("Entries").getJSONObject(0));
        } else {
            throw new TwikeyClient.UserException(apiError(response));
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#transaction-status">API Documentation</a>
     *
     * <p>Retrieves transaction status by ID, ref, or mandate ID.</p>
     *
     * <p>This method queries the Twikey API for the latest details related to the mandate, invoice, etc.
     * for the provided identifier. Typically used for querying status based on ID, reference, or mandate.</p>
     *
     * @param newTransactionRequest an object representing information for identifying the transaction
     * @return a structured {@link TransactionResponse.Transaction} object representing the server’s reply
     * @throws IOException                if an I/O error occurs during the request
     * @throws TwikeyClient.UserException if the API call fails or the identifier is invalid
     */
    public TransactionResponse.Transaction status(TransactionRequests.StatusRequest newTransactionRequest) throws IOException, TwikeyClient.UserException {
        String tx = getPostDataString(newTransactionRequest.toParams());
        tx += newTransactionRequest.toInclude();

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transaction/detail?%s".formatted(tx)))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return TransactionResponse.Transaction.fromJson(json.getJSONArray("Entries").getJSONObject(0));
        } else {
            throw new TwikeyClient.UserException(apiError(response));
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#action-on-transaction">API Documentation</a>
     *
     * <p>Trigger a specific action on an existing transaction.</p>
     *
     * <p>This endpoint allows initiating predefined actions related to a transaction, such as reoffer
     * or archive the transaction. The action type must be explicitly provided in the request.</p>
     *
     * @param action the {@link TransactionRequests.ActionRequest} containing the transaction ID and action
     * @throws IOException                if an I/O error occurs during the request
     * @throws TwikeyClient.UserException if the API returns an error or the request fails
     */
    public void action(TransactionRequests.ActionRequest action) throws IOException, TwikeyClient.UserException {
        Map<String, String> requestMap = action.toRequest();

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transaction/action"))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(requestMap)))
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            throw new TwikeyClient.UserException(apiError(response));
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#update-transaction">API Documentation</a>
     *
     * <p>Send a PUT request to update existing transaction details.</p>
     *
     * <p>This endpoint allows modifying transaction information such as message or linked references.
     * Only provide parameters for fields you wish to update.
     * Some fields may have special behavior or limitations depending on the object state.</p>
     *
     * @param update the {@link TransactionRequests.UpdateTransactionRequest} containing the transaction details to update
     * @throws IOException                if an I/O error occurs during the request
     * @throws TwikeyClient.UserException if the API returns an error or the request fails
     */
    public void update(TransactionRequests.UpdateTransactionRequest update) throws IOException, TwikeyClient.UserException {
        Map<String, String> requestMap = update.toRequest();
        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transaction"))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .PUT(HttpRequest.BodyPublishers.ofString(getPostDataString(requestMap)))
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new TwikeyClient.UserException(apiError(response));
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#refund-a-transaction">API Documentation</a>
     *
     * <p>Creates a refund for a given transaction via a POST request to the API.</p>
     *
     * <p>If the beneficiary account does not exist yet,
     * it will be registered to the customer using the mandate IBAN or the one provided.</p>
     *
     * @param refundRequest the {@link TransactionRequests.RefundRequest} containing the refund payload.
     *                      Must include 'id', 'message', and 'amount'. May include 'ref', 'place', 'iban', or 'bic'.
     * @return {@link TransactionResponse.Refund} containing the refund entry details
     * @throws IOException                if an I/O error occurs during the request
     * @throws TwikeyClient.UserException if the API returns an error or the request fails
     */
    public TransactionResponse.Refund refund(TransactionRequests.RefundRequest refundRequest) throws IOException, TwikeyClient.UserException {
        Map<String, String> requestMap = refundRequest.toRequest();
        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transaction/refund"))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(requestMap)))
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return TransactionResponse.Refund.fromJson(json.getJSONArray("Entries").getJSONObject(0));
        } else {
            throw new TwikeyClient.UserException(apiError(response));
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#remove-a-transaction">API Documentation</a>
     *
     * <p>Sends a DELETE request to remove a transaction that has not yet been sent to the bank on the Twikey API.</p>
     *
     * <p>This method allows the creditor to cancel/delete a resource by providing the unique ID.
     * Typically used to delete/cancel objects like an agreement, an invoice, or a payment link.
     * Raises an error if the API response contains an error code or the request fails.</p>
     *
     * @param delete the {@link TransactionRequests.RemoveTransactionRequest} containing information to identify the transaction.
     * @throws IOException                if an I/O error occurs during the request
     * @throws TwikeyClient.UserException if the API returns an error or the request fails
     */
    public void delete(TransactionRequests.RemoveTransactionRequest delete) throws IOException, TwikeyClient.UserException {
        Map<String, String> requestMap = delete.toRequest();
        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transaction?%s".formatted(getPostDataString(requestMap))))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .DELETE()
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new TwikeyClient.UserException(apiError(response));
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#query-transactions">API Documentation</a>
     *
     * <p>Retrieve all created transactions starting from a specific transaction ID.</p>
     *
     * <p>This endpoint allows you to search for transactions based on specific identifiers.
     * The result contains a list of transactions that match the provided parameters.</p>
     *
     * @param newTransactionRequest the {@link TransactionRequests.QueryRequest} representing the request payload
     * @return a {@link List} of {@link TransactionResponse.Transaction} objects representing the server’s reply
     * @throws IOException                if an I/O error occurs during the request
     * @throws TwikeyClient.UserException if the API returns an error or the request fails
     */
    public List<TransactionResponse.Transaction> query(TransactionRequests.QueryRequest newTransactionRequest) throws IOException, TwikeyClient.UserException {
        String tx = getPostDataString(newTransactionRequest.toRequest());

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transaction/query?%s".formatted(tx)))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return TransactionResponse.Transaction.fromQuery(json);
        } else {
            throw new TwikeyClient.UserException(apiError(response));
        }
    }

    /**
     * Get updates about all mandates (new/updated/cancelled)
     *
     * @param callback Callback for every change
     * @param sideloads items to include in the sideloading @link <a href="https://www.twikey.com/api/#transaction-feed">www.twikey.com/api/#transaction-feed</a>
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(TransactionCallback callback,String... sideloads) throws IOException, TwikeyClient.UserException {
        boolean isEmpty;
        do {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(twikeyClient.getUrl("/transaction", sideloads))
                    .header("Content-Type", HTTP_FORM_ENCODED)
                    .header("User-Agent", twikeyClient.getUserAgent())
                    .header("Authorization", twikeyClient.getSessionToken())
                    .GET()
                    .build();

            HttpResponse<InputStream> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            int responseCode = response.statusCode();
            if (responseCode == 200) {
                try {
                    JSONObject json = new JSONObject(new JSONTokener(response.body()));
                    JSONArray messagesArr = json.getJSONArray("Entries");
                    isEmpty = messagesArr.isEmpty();
                    if (!isEmpty) {
                        for (int i = 0; i < messagesArr.length(); i++) {
                            JSONObject obj = messagesArr.getJSONObject(i);
                            callback.transaction(TransactionResponse.Transaction.fromJson(obj));
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new TwikeyClient.UserException(apiError(response));
            }
        } while (!isEmpty);
    }
}
