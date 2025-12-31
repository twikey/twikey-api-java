package com.twikey;

import com.twikey.callback.RefundCallback;
import com.twikey.modal.RefundRequests;
import com.twikey.modal.RefundResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.twikey.TwikeyClient.HTTP_FORM_ENCODED;
import static com.twikey.TwikeyClient.getPostDataString;
public class RefundGateway {

    private final TwikeyClient twikeyClient;

    protected RefundGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * Creation of a refund provided the customer was created and has a customerNumber
     * @param refundRequest required
     * <ul>
     * <li>customerNumber	The customer number</li>
     * <li>iban	Iban of the beneficiary</li>
     * <li>message	Message to the creditor	Yes	string </li>
     * <li>amount	Amount to be send</li>
     * <li>ref	Reference of the transaction</li>
     * <li>date	Required execution date of the transaction (ReqdExctnDt)</li>
     * <li>place	Optional place</li>
     * </ul>
     * @return json object containing <pre>{
     *             "id": "11DD32CA20180412220109485",
     *             "iban": "BE68068097250734",
     *             "bic": "JVBABE22",
     *             "amount": 12,
     *             "msg": "test",
     *             "place": null,
     *             "ref": "123",
     *             "date": "2018-04-12"
     *         }</pre>
     * @throws IOException   When no connection could be made
     * @throws com.twikey.TwikeyClient.UserException When Twikey returns a user error (400)
     */
    public RefundResponse.Refund create(RefundRequests.NewCreditTransferRequest refundRequest) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = refundRequest.toRequestMap();

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transfer"))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(params)))
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());


        int responseCode = response.statusCode();
        if (responseCode == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return RefundResponse.Refund.fromJson(json.getJSONArray("Entries").getJSONObject(0));
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public RefundResponse.Refund details(String id) throws IOException, TwikeyClient.UserException {

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transfer/detail?%s".formatted(id)))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return RefundResponse.Refund.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public void remove(String id) throws IOException, TwikeyClient.UserException {

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transfer?%s".formatted(id)))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .DELETE()
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
     * TODO
     */
    public RefundResponse.CreditTransferResponse createBatch(RefundRequests.CompleteCreditTransferRequest createBatchRequest) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = createBatchRequest.toRequest();

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transfer/complete"))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(params)))
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return RefundResponse.CreditTransferResponse.fromJson(json.getJSONArray("CreditTransfers").getJSONObject(0));
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public RefundResponse.CreditTransferResponse batchDetails(RefundRequests.CompleteCreditTransferDetailsRequest BatchRequest) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = BatchRequest.toRequest();

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transfer/complete?%s".formatted(getPostDataString(params))))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return RefundResponse.CreditTransferResponse.fromJson(json.getJSONArray("CreditTransfers").getJSONObject(0));
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * Creation a beneficiary account (with accompanied customer)
     * @param beneficiary required

     * @return json object containing <pre>{
     *     "name": "Beneficiary Name",
     *     "iban": "BE68068897250734",
     *     "bic": "JVBABE22",
     *     "available": true,
     *     "address": {
     *         "street": "Veldstraat 11",
     *         "city": "Gent",
     *         "zip": "9000",
     *         "country": "BE"
     *     }
     * }</pre>
     * @throws IOException   When no connection could be made
     * @throws com.twikey.TwikeyClient.UserException When Twikey returns a user error (400)
     */
    public RefundResponse.AddBeneficiaryResponse createBeneficiaryAccount(RefundRequests.AddBeneficiaryRequest beneficiary) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = beneficiary.toRequestMap();

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transfers/beneficiaries"))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(params)))
                .build();

        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return RefundResponse.AddBeneficiaryResponse.fromJson(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public List<RefundResponse.AddBeneficiaryResponse> getBeneficiaries(Boolean withAddress) throws IOException, TwikeyClient.UserException {

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transfer/beneficiaries?withAddress=%s".formatted(withAddress)))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .GET()
                .build();
        HttpResponse<String> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(new JSONTokener(response.body()));
            return RefundResponse.AddBeneficiaryResponse.fromQuery(json);
        } else {
            String apiError = response.headers()
                    .firstValue("ApiError")
                    .orElse("Twikey status=" + response.statusCode());
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * TODO
     */
    public void disableBeneficiary(RefundRequests.DisableBeneficiaryRequest beneficiaryRequest) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = beneficiaryRequest.toRequest();
        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transfer/beneficiaries?%s".formatted(getPostDataString(params))))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .DELETE()
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
     * Get updates about all paid refunds
     *
     * @param callback Callback for every payment
     * @param sideloads items to include in the sideloading @link <a href="https://www.twikey.com/api/#transaction-feed">www.twikey.com/api/#transaction-feed</a>
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(RefundCallback callback, String... sideloads) throws IOException, TwikeyClient.UserException {

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/transfer", sideloads))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .build();
        boolean isEmpty;
        do{
            HttpResponse<InputStream> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            int responseCode = response.statusCode();
            if (responseCode == 200) {
                JSONObject json = new JSONObject(new JSONTokener(response.body()));

                JSONArray messagesArr = json.getJSONArray("Entries");
                isEmpty = messagesArr.isEmpty();
                if (!isEmpty) {
                    for (int i = 0; i < messagesArr.length(); i++) {
                        JSONObject obj = messagesArr.getJSONObject(i);
                        callback.refund(obj);
                    }
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
