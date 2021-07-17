package com.twikey;

import com.twikey.callback.PaylinkCallback;
import com.twikey.modal.Customer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.twikey.TwikeyClient.getPostDataString;

public class PaylinkGateway {

    private final TwikeyClient twikeyClient;

    protected PaylinkGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
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
     * @param ct          Template to use can be found @ https://www.twikey.com/r/admin#/c/template
     * @param customer    Customer details
     * @param linkDetails Map containing any of the parameters in the above table
     * @return Url to redirect the customer to or to send in an email
     * @throws IOException   When no connection could be made
     * @throws com.twikey.TwikeyClient.UserException When Twikey returns a user error (400)
     */
    public JSONObject create(long ct, Customer customer, Map<String, String> linkDetails) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = new HashMap<>(linkDetails);
        params.put("ct", String.valueOf(ct));
        if (customer != null) {
            params.put("customerNumber", customer.getNumber());
            params.put("email", customer.getEmail());
            params.put("firstname", customer.getFirstname());
            params.put("lastname", customer.getLastname());
            params.put("l", customer.getLang());
            params.put("address", customer.getStreet());
            params.put("city", customer.getCity());
            params.put("zip", customer.getZip());
            params.put("country", customer.getCountry());
            params.put("mobile", customer.getMobile());
            if(customer.getCompanyName() != null){
                params.put("companyName", customer.getCompanyName());
                params.put("coc", customer.getCoc());
            }
        }

        URL myurl = twikeyClient.getUrl("/payment/link");
        HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("User-Agent", twikeyClient.getUserAgent());
        con.setRequestProperty("Authorization", twikeyClient.getSessionToken());
        con.setDoOutput(true);
        con.setDoInput(true);

        try (DataOutputStream output = new DataOutputStream(con.getOutputStream())) {
            output.writeBytes(getPostDataString(params));
            output.flush();
        }

        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                /* {
                  "mndtId": "COREREC01",
                  "url": "http://twikey.to/myComp/ToYG",
                  "key": "ToYG"
                } */
                return new JSONObject(new JSONTokener(br));
            }
        } else {
            String apiError = con.getHeaderField("ApiError");
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * Get updates about all links
     *
     * @param callback Callback for every change
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(PaylinkCallback callback) throws IOException, TwikeyClient.UserException {
        URL myurl = twikeyClient.getUrl("/payment/link/feed");
        boolean isEmpty;
        do {
            HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("User-Agent", twikeyClient.getUserAgent());
            con.setRequestProperty("Authorization", twikeyClient.getSessionToken());

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    JSONObject json = new JSONObject(new JSONTokener(br));

                    JSONArray messagesArr = json.getJSONArray("Links");
                    isEmpty = messagesArr.isEmpty();
                    if (!isEmpty) {
                        for (int i = 0; i < messagesArr.length(); i++) {
                            JSONObject obj = messagesArr.getJSONObject(i);
                            callback.paylink(obj);
                        }
                    }
                }
            } else {
                String apiError = con.getHeaderField("ApiError");
                throw new TwikeyClient.UserException(apiError);
            }
        } while (!isEmpty);
    }
}
