package com.twikey;

import com.twikey.callback.RefundCallback;
import com.twikey.modal.Account;
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
public class RefundGateway {

    private final TwikeyClient twikeyClient;

    protected RefundGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * Creation of a refund provided the customer was created and has a customerNumber
     * @param customerNumber required
     * @param transactionDetails required
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
    public JSONObject create(String customerNumber, Map<String, String> transactionDetails) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = new HashMap<>(transactionDetails);
        params.put("customerNumber", customerNumber);

        URL myurl = twikeyClient.getUrl("/transfer");
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
     * Creation a beneficiary account (with accompanied customer)
     * @param customer required
     * @param account required

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
    public JSONObject createBeneficiaryAccount(Customer customer, Account account) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = new HashMap<>(customer.asFormParameters());
        params.put("iban",account.getIban());
        params.put("bic",account.getBic());

        URL myurl = twikeyClient.getUrl("/transfers/beneficiaries");
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
                return new JSONObject(new JSONTokener(br));
            }
        } else {
            String apiError = con.getHeaderField("ApiError");
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
        URL myurl = twikeyClient.getUrl("/transfer",sideloads);
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
                            callback.refund(obj);
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
