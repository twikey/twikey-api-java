package com.twikey;

import static com.twikey.TwikeyClient.getPostDataString;

import com.twikey.callback.DocumentCallback;
import com.twikey.modal.Customer;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DocumentGateway {

    private final TwikeyClient twikeyClient;

    protected DocumentGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * <ul>
     * <li>iban	International Bank Account Number of the debtor	No	string</li>
     * <li>bic	Bank Identifier Code of the IBAN	No	string</li>
     * <li>mandateNumber	Mandate Identification number (if not generated)	No	string</li>
     * <li>contractNumber	The contract number which can override the one defined in the template.	No	string</li>
     * <li>campaign	Campaign to include this url in	No	string</li>
     * <li>prefix	Optional prefix to use in the url (default companyname)	No	string</li>
     * <li>check	If a mandate already exists, don't prepare a new one (based on email, customerNumber or mandatenumber and + template type(=ct))	No	boolean</li>
     * <li>reminderDays	Send a reminder if contract was not signed after number of days	No	number</li>
     * <li>sendInvite	Send out invite email directly	No	boolean</li>
     * <li>document	Add a contract in base64 format	No	string</li>
     * <li>amount	In euro for a transaction via a first payment or post signature via an SDD transaction	No	string</li>
     * <li>token	(optional) token to be returned in the exit-url (lenght &lt; 100)	No	string</li>
     * <li>requireValidation	Always start with the registration page, even with all known mandate details	No	boolean</li>
     * </ul>
     *
     * @param ct             Template to use can be found @ https://www.twikey.com/r/admin#/c/template
     * @param customer       Customer details
     * @param mandateDetails Map containing any of the parameters in the above table
     * @throws IOException   When no connection could be made
     * @throws com.twikey.TwikeyClient.UserException When Twikey returns a user error (400)
     * @return Url to redirect the customer to or to send in an email
     */
    public JSONObject create(long ct, Customer customer, Map<String, String> mandateDetails) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = new HashMap<>(mandateDetails);
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

        URL myurl = twikeyClient.getUrl("/invite");
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
     * Get updates about all mandates (new/updated/cancelled) and reset feed
     *
     * @param callback Callback for every change
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(final DocumentCallback callback, final LocalDateTime resetToDate) throws IOException, TwikeyClient.UserException {
        HttpURLConnection con = getConnectionForFeed(resetToDate);
        processOutput(con, callback);
    }

    /**
     * Get updates about all mandates (new/updated/cancelled)
     *
     * @param mandateCallback Callback for every change
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(final DocumentCallback mandateCallback) throws IOException, TwikeyClient.UserException {
        HttpURLConnection con = getConnectionForFeed(null);
        processOutput(con, mandateCallback);
    }

    private void processOutput(final HttpURLConnection con, final DocumentCallback callback) throws IOException, TwikeyClient.UserException {
        boolean isEmpty;
        do {

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    JSONObject json = new JSONObject(new JSONTokener(br));

                    JSONArray messagesArr = json.getJSONArray("Messages");
                    isEmpty = messagesArr.isEmpty();
                    if (!isEmpty) {
                        for (int i = 0; i < messagesArr.length(); i++) {
                            JSONObject obj = messagesArr.getJSONObject(i);
                            if (obj.has("CxlRsn")) {
                                callback.cancelledDocument(obj);
                            } else if (obj.has("AmdmntRsn")) {
                                callback.updatedDocument(obj);
                            } else {
                                callback.newDocument(obj);
                            }
                        }
                    }
                }
            } else {
                String apiError = con.getHeaderField("ApiError");
                throw new TwikeyClient.UserException(apiError);
            }
        } while (!isEmpty);
    }

    private HttpURLConnection getConnectionForFeed(final LocalDateTime resetToDate) throws IOException, TwikeyClient.UserException {
        URL myurl = twikeyClient.getUrl("/mandate");
        HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("User-Agent", twikeyClient.getUserAgent());
        con.setRequestProperty("Authorization", twikeyClient.getSessionToken());
        if(resetToDate != null) con.setRequestProperty(TwikeyClient.X_RESET, TwikeyClient.formatResetAndSetToUTC(resetToDate));
        return con;
    }
}
