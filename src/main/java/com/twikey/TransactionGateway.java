package com.twikey;

import com.twikey.callback.TransactionCallback;
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

public class TransactionGateway {

    private final TwikeyClient twikeyClient;

    protected TransactionGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * @param mandateNumber required
     * @param transactionDetails map with keys (message,ref,amount,place)
     * @return json object containing <pre>{
     *                       "id": 381563,
     *                       "contractId": 325638,
     *                       "mndtId": "MNDT123",
     *                       "contract": "Algemene voorwaarden",
     *                       "amount": 10.0,
     *                       "msg": "Monthly payment",
     *                       "place": null,
     *                       "ref": null,
     *                       "date": "2017-09-16T14:32:05Z"
     *                     }</pre>
     * @throws IOException   When no connection could be made
     * @throws com.twikey.TwikeyClient.UserException When Twikey returns a user error (400)
     */
    public JSONObject create(String mandateNumber, Map<String, String> transactionDetails) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = new HashMap<>(transactionDetails);
        params.put("mndtId", mandateNumber);

        URL myurl = twikeyClient.getUrl("/transaction");
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
                return new JSONObject(new JSONTokener(br)).getJSONArray("Entries").optJSONObject(0);
            }
        } else {
            String apiError = con.getHeaderField("ApiError");
            throw new TwikeyClient.UserException(apiError);
        }
    }

    /**
     * Get updates about all mandates (new/updated/cancelled)
     *
     * @param callback Callback for every change
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(TransactionCallback callback,String... sideloads) throws IOException, TwikeyClient.UserException {
        URL myurl = twikeyClient.getUrl("/transaction",sideloads);
        boolean isEmpty;
        do{
            HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("User-Agent", twikeyClient.getUserAgent());
            con.setRequestProperty("Authorization", twikeyClient.getSessionToken());

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    JSONObject json = new JSONObject(new JSONTokener(br));

                    JSONArray messagesArr = json.getJSONArray("Entries");
                    isEmpty = messagesArr.isEmpty();
                    if (!isEmpty) {
                        for (int i = 0; i < messagesArr.length(); i++) {
                            JSONObject obj = messagesArr.getJSONObject(i);
                            callback.transaction(obj);
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
