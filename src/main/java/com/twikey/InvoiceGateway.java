package com.twikey;

import com.twikey.callback.InvoiceCallback;
import com.twikey.modal.InvoiceRequests;
import com.twikey.modal.InvoiceResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Map;

import static com.twikey.TwikeyClient.*;

public class InvoiceGateway {

    private final TwikeyClient twikeyClient;

    protected InvoiceGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * @param create A map containing all the information to create that invoice
     * @return jsonobject <pre>{
     *                       "id": "fec44175-b4fe-414c-92aa-9d0a7dd0dbf2",
     *                       "number": "Inv20200001",
     *                       "title": "Invoice July",
     *                       "ct": 1988,
     *                       "amount": "100.00",
     *                       "date": "2020-01-31",
     *                       "duedate": "2020-02-28",
     *                       "status": "BOOKED",
     *                       "manual": true,
     *                       "url": "<a href="https://yourpage.beta.twikey.com/invoice.html?fec44175-b4fe-414c-92aa-9d0a7dd0dbf2">Invoice</a>"
     *                   }</pre>
     * @throws IOException                           When no connection could be made
     * @throws TwikeyClient.UserException When Twikey returns a user error (400)
     */
    public InvoiceResponse.Invoice create(InvoiceRequests.CreateInvoiceRequest create) throws IOException, TwikeyClient.UserException {
        JSONObject requestMap = create.toRequest();

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/invoice"))
                .header("Content-Type", HTTP_APPLICATION_JSON)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(requestMap)))
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return InvoiceResponse.Invoice.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#update-invoice">Twikey API - Update Invoice</a>
     * <p>
     * Sends a PUT request to update existing invoice details.
     * <p>
     * This endpoint allows modifying invoice information such as title,
     * pdf, or linked references. Only provide parameters for fields you
     * wish to update. Some fields may have special behavior or limitations
     * depending on the invoice state.
     *
     * @param update A request model containing the payload with updated invoice fields.
     * @return An {@link InvoiceResponse.Invoice} representing the server's reply.
     * @throws IOException                If a network error occurs during the request.
     * @throws TwikeyClient.UserException If the API returns an error response.
     */
    public InvoiceResponse.Invoice update(InvoiceRequests.UpdateInvoiceRequest update) throws IOException, TwikeyClient.UserException {

        JSONObject requestMap = update.toRequest();
        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/invoice/%s".formatted(requestMap.get("id"))))
                .header("Content-Type", HTTP_APPLICATION_JSON)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .PUT(HttpRequest.BodyPublishers.ofString(String.valueOf(requestMap)))
                .build();

        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return InvoiceResponse.Invoice.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#delete-invoice">Twikey API - Delete Invoice</a>
     * <p>
     * Sends a DELETE request to remove an invoice on the Twikey API.
     * <p>
     * This method allows the creditor to cancel or delete a resource by providing its unique ID.
     * Typically used to delete/cancel objects such as an agreement, an invoice, or a payment link.
     * An error is thrown if the API response contains an error code or the request fails.
     *
     * @param delete The unique identifier of the invoice to cancel.
     * @throws IOException                If a network error occurs during the request.
     * @throws TwikeyClient.UserException If the API returns an error response.
     */
    public void delete(String delete) throws IOException, TwikeyClient.UserException {

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/invoice/%s".formatted(delete))).DELETE()
                .header("Content-Type", HTTP_APPLICATION_JSON)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#invoice-details">Twikey API - Invoice Details</a>
     * <p>
     * Retrieves the details of a specific invoice by ID or number, optionally including last payment,
     * metadata, or customer data.
     * <p>
     * This method queries the Twikey API for the latest details related to a mandate, invoice,
     * or other resource using the provided identifier. Typically used to check the status of an
     * invoice based on ID, reference, or mandate.
     *
     * @param details An {@link InvoiceRequests.InvoiceDetailRequest} object representing
     *                the information required to identify the invoice.
     * @return {@link InvoiceResponse.Invoice} A structured response object representing the server’s reply.
     * @throws IOException                If a network error occurs during the request.
     * @throws TwikeyClient.UserException If the API returns an error response.
     */
    public InvoiceResponse.Invoice details(InvoiceRequests.InvoiceDetailRequest details) throws IOException, TwikeyClient.UserException {

        Map<String, String> params = details.toRequest();
        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/invoice/%s?%s".formatted(params.get("invoice"), params.get("include"))))
                .header("Content-Type", HTTP_APPLICATION_JSON)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();

        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return InvoiceResponse.Invoice.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#action-on-invoice">Twikey API - action-on-invoice</a>
     * <p>
     * Trigger a specific action on an existing invoice.
     * <p>
     * This endpoint allows initiating predefined actions related to an invoice,
     * such as sending an invitation or reminder. The action type must be explicitly
     * provided in the request.
     *
     * @param action The action request containing the invoice ID and the type of action to perform.
     * @throws IOException                If a network error occurs while making the request.
     * @throws TwikeyClient.UserException If the API rejects the request or returns a user-related error.
     */
    public void action(InvoiceRequests.InvoiceActionRequest action) throws IOException, TwikeyClient.UserException {

        Map<String, String> params = action.toRequest();
        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/invoice/%s/action".formatted(params.get("id"))))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(params)))
                .build();

        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#upload-ubl">Twikey API - upload-ubl</a>
     * <p>
     * Add new invoices via a UBL file during a POST request to the API.
     * <p>
     * This endpoint allows uploading a UBL (Universal Business Language) file
     * containing invoice data. Typically used for automating invoice creation
     * from external accounting or ERP systems.
     *
     * @param Ubl An object representing the payload for the request containing the UBL file.
     * @return Invoice A structured response object representing the server’s reply.
     * @throws IOException                If a network error occurs while making the request.
     * @throws TwikeyClient.UserException If the API rejects the request or returns a user-related error.
     */
    public InvoiceResponse.Invoice uploadUbl(InvoiceRequests.UblUploadRequest Ubl) throws IOException, TwikeyClient.UserException {

        Map<String, String> headers = Ubl.toHeaders();
        HttpRequest.Builder builder = HttpRequest.newBuilder(twikeyClient.getUrl("/invoice/ubl"))
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofFile(Path.of(Ubl.getXmlPath())));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
        HttpRequest request = builder.build();

        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return InvoiceResponse.Invoice.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#bulk-create-invoices">Twikey API - bulk-create-invoices</a>
     * <p>
     * Creates multiple invoices in a single batch upload.
     * <p>
     * This endpoint is used for creating invoices in bulk. It allows submitting
     * multiple invoice requests at once, reducing the number of API calls
     * required for large-scale invoice processing.
     *
     * @param batch A BulkInvoiceRequest object containing a list of invoice requests.
     * @return String The batchId of the created batch.
     * @throws IOException                If a network error occurs while making the request.
     * @throws TwikeyClient.UserException If the API rejects the request or returns a user-related error.
     */
    public String createBatch(InvoiceRequests.BulkInvoiceRequest batch) throws IOException, TwikeyClient.UserException {
        JSONArray jsonArray = batch.toRequest();
        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/invoice/bulk"))
                .header("Content-Type", HTTP_APPLICATION_JSON)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(jsonArray)))
                .build();

        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return new JSONObject(new JSONTokener(response.body())).getString("batchId");
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * See <a href="https://www.twikey.com/api/#bulk-batch-details">Twikey API - bulk-batch-details</a>
     * <p>
     * Retrieves the result of a bulk invoice upload by batch ID.
     * <p>
     * This endpoint is used to check the processing status of each invoice
     * within a previously uploaded batch. It is typically used to confirm
     * whether invoices were successfully created or failed during batch upload.
     *
     * @param batchId The unique batch ID returned when the bulk invoice upload was created.
     * @return BulkInvoiceDetail containing invoice identifiers to their respective statuses.
     * @throws IOException                If a network error occurs while making the request.
     * @throws TwikeyClient.UserException If the API rejects the request or returns a user-related error.
     */
    public InvoiceResponse.BulkInvoiceDetail batchDetails(String batchId) throws IOException, TwikeyClient.UserException {

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/invoice/bulk?batchId=%s".formatted(batchId)))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();

        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONArray array = new JSONArray(new JSONTokener(response.body()));
            return InvoiceResponse.BulkInvoiceDetail.fromJson(batchId, array);
        } else if (response.statusCode() == 409) {
            return InvoiceResponse.BulkInvoiceDetail.PENDING;
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }


    /**
     * Get updates about all mandates (new/updated/cancelled)
     *
     * @param invoiceCallback Callback for every change
     * @param sideloads       items to include in the sideloading @link <a href="https://www.twikey.com/api/#invoice-feed">www.twikey.com/api/#invoice-feed</a>
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(InvoiceCallback invoiceCallback, String... sideloads) throws IOException, TwikeyClient.UserException {

        boolean isEmpty;
        do {
            HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/invoice", sideloads))
                    .headers("Content-Type", HTTP_FORM_ENCODED)
                    .headers("User-Agent", twikeyClient.getUserAgent())
                    .headers("Authorization", twikeyClient.getSessionToken())
                    .GET()
                    .build();
            HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
            int responseCode = response.statusCode();
            if (responseCode == 200) {
                try {
                    JSONObject json = new JSONObject(new JSONTokener(response.body()));

                    JSONArray invoicesArr = json.getJSONArray("Invoices");
                    isEmpty = invoicesArr.isEmpty();
                    if (!invoicesArr.isEmpty()) {
                        for (int i = 0; i < invoicesArr.length(); i++) {
                            JSONObject obj = invoicesArr.getJSONObject(i);
                            invoiceCallback.invoice(InvoiceResponse.Invoice.fromJson(obj));
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String apiError = response.headers()
                        .firstValue("ApiError")
                        .orElse("Twikey status=" + response.statusCode());
                throw new TwikeyClient.UserException(apiError);
            }
        } while (!isEmpty);
    }
}
