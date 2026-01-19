package com.twikey;

import com.twikey.callback.PaylinkCallback;
import com.twikey.modal.DocumentRequests;
import com.twikey.modal.PaylinkRequests;
import com.twikey.modal.PaylinkResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static com.twikey.TwikeyClient.HTTP_FORM_ENCODED;
import static com.twikey.TwikeyClient.apiError;
import static com.twikey.TwikeyClient.getPostDataString;

public class PaylinkGateway {

    private final TwikeyClient twikeyClient;

    protected PaylinkGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * @deprecated Use {@link #create(PaylinkRequests.PaylinkRequest)} instead.
     * <ul>
     * <li>title	Message to the debtor [*1]	Yes	string (200)</li>
     * <li>remittance	Payment message, if empty then title will be used [*2]	No	string</li>
     * <li>amount	Amount to be billed	Yes	string</li>
     * <li>redirectUrl	Optional redirect after pay url (must use http(s)://)	No	url</li>
     * <li>place	Optional place	No	string</li>
     * <li>expiry	Optional expiration date	No	date</li>
     * <li>sendInvite	Send out invite email or sms directly (email, sms)	No	string</li>
     * <li>method	Circumvents the payment selection with PSP (bancontact/ideal/maestro/mastercard/visa/inghomepay/kbc/belfius)	No	string</li>
     * <li>invoice	create payment link for specific invoice number	No	string</li>
     * </ul>
     *
     * @param ct          <a href="https://www.twikey.com/r/admin#/c/template">Template to use can be found</a>
     * @param customer    Customer details
     * @param linkDetails Map containing any of the parameters in the above table
     * @return JSONObject representing the created paylink
     * @throws IOException   When no connection could be made
     * @throws TwikeyClient.UserException When Twikey returns a user error (400)
     */
    @Deprecated
    public JSONObject create(long ct, DocumentRequests.Customer customer, Map<String, String> linkDetails) throws IOException, TwikeyClient.UserException {
        PaylinkRequests.PaylinkRequest paylinkRequest = new PaylinkRequests.PaylinkRequest(ct, customer, linkDetails);
        return createPaylink(paylinkRequest);
    }

    public PaylinkResponse.Paylink create(PaylinkRequests.PaylinkRequest paylinkRequest) throws IOException, TwikeyClient.UserException {
        JSONObject json = createPaylink(paylinkRequest);
        return PaylinkResponse.Paylink.fromJson(json);
    }

    private JSONObject createPaylink(PaylinkRequests.PaylinkRequest paylinkRequest) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = paylinkRequest.toRequest();

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/payment/link"))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .POST(HttpRequest.BodyPublishers.ofString(getPostDataString(params)))
                .build();

        HttpResponse<InputStream> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        int responseCode = response.statusCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(response.body()))) {
                return new JSONObject(new JSONTokener(br));
            }
        } else {
            throw new TwikeyClient.UserException(apiError(response));
        }
    }

    /**
     * Get updates about all links
     *
     * @param callback Callback for every change
     * @param sideloads items to include in the sideloading @link <a href="https://www.twikey.com/api/#paymentlink-feed">www.twikey.com/api/#paymentlink-feed</a>
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(PaylinkCallback callback,String... sideloads) throws IOException, TwikeyClient.UserException {

        HttpRequest request = HttpRequest.newBuilder(twikeyClient.getUrl("/payment/link/feed", sideloads))
                .header("Content-Type", HTTP_FORM_ENCODED)
                .header("User-Agent", twikeyClient.getUserAgent())
                .header("Authorization", twikeyClient.getSessionToken())
                .build();

        boolean isEmpty;
        do {
            HttpResponse<InputStream> response = twikeyClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            int responseCode = response.statusCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(response.body()))) {
                    JSONObject json = new JSONObject(new JSONTokener(br));

                    JSONArray messagesArr = json.getJSONArray("Links");
                    isEmpty = messagesArr.isEmpty();
                    if (!isEmpty) {
                    for (int i = 0; i < messagesArr.length(); i++) {
                        JSONObject obj = messagesArr.getJSONObject(i);
                        callback.paylink(obj);
                        callback.paylink(PaylinkResponse.Paylink.fromJson(obj));
                    }

                    }
                }
            } else {
            throw new TwikeyClient.UserException(apiError(response));

            }
        } while (!isEmpty);
    }
}
