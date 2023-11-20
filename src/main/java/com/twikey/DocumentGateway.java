package com.twikey;

import com.twikey.callback.DocumentCallback;
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

public class DocumentGateway {

    private final TwikeyClient twikeyClient;

    protected DocumentGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * <ul>
     * <li>iban	International Bank Account Number of the debtor</li>
     * <li>bic	Bank Identifier Code of the IBAN</li>
     * <li>mandateNumber	Mandate Identification number (if not generated)</li>
     * <li>contractNumber	The contract number which can override the one defined in the template.</li>
     * <li>campaign	Campaign to include this url in</li>
     * <li>prefix	Optional prefix to use in the url (default companyname)</li>
     * <li>check	If a mandate already exists, don't prepare a new one (based on email, customerNumber or mandatenumber and + template type(=ct))</li>
     * <li>reminderDays	Send a reminder if contract was not signed after number of days</li>
     * <li>sendInvite	Send out invite email directly</li>
     * <li>document	Add a contract in base64 format</li>
     * <li>amount	In euro for a transaction via a first payment or post signature via an SDD transaction</li>
     * <li>token	(optional) token to be returned in the exit-url (lenght &lt; 100)</li>
     * <li>requireValidation	Always start with the registration page, even with all known mandate details</li>
     * </ul>
     *
     * @param ct             Template to use can be found @ <a href="https://www.twikey.com/r/admin#/c/template">https://www.twikey.com/r/admin#/c/template</a>
     * @param customer       Customer details
     * @param mandateDetails Map containing any of the parameters in the above table
     * @throws IOException   When no connection could be made
     * @throws com.twikey.TwikeyClient.UserException When Twikey returns a user error (400)
     * @return Url to redirect the customer to or to send in an email
     * @throws IOException A network error occurred
     * @throws TwikeyClient.UserException A Twikey generated user error occurred
     */
    public JSONObject create(long ct, Customer customer, Map<String, String> mandateDetails) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = new HashMap<>(mandateDetails);
        params.put("ct", String.valueOf(ct));
        if (customer != null) {
            params.putAll(customer.asFormParameters());
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
     * Same parameters as invite, but some extra might be required depending on the method
     * <ul>
     * <li><b>method (required)</b>:	Method to sign (sms/digisign/import/itsme/emachtiging/paper,...)</li>
     * <li>digsig:	Wet signature (PDF encoded as base64) required if method is digisign</li>
     * <li>key:	shortcode from the invite url. Use this parameter instead of 'mandateNumber' to directly sign a prepared mandate.</li>
     * <li>bic:	Required for methods emachtiging, iDeal and iDIn</li>
     * <li>signDate:	Date of signature (xsd:dateTime), optional for sms as it uses date of reply</li>
     * <li>place:	Place of signature</li>
     * </ul>
     *
     * @param ct             Template to use can be found @ <a href="https://www.twikey.com/r/admin#/c/template">https://www.twikey.com/r/admin#/c/template</a>
     * @param customer       Customer details
     * @param mandateDetails Map containing any of the parameters in the above table
     * @return Url to redirect the customer to or to send in an email
     * @throws IOException A network error occurred
     * @throws TwikeyClient.UserException A Twikey generated user error occurred
     */
    public JSONObject sign(long ct, Customer customer, Map<String, String> mandateDetails) throws IOException, TwikeyClient.UserException {
        Map<String, String> params = new HashMap<>(mandateDetails);
        params.put("ct", String.valueOf(ct));
        if (customer != null) {
            params.putAll(customer.asFormParameters());
        }

        URL myurl = twikeyClient.getUrl("/sign");
        HttpURLConnection con = (HttpURLConnection)myurl.openConnection();
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
     * Get updates about all mandates (new/updated/cancelled)
     *
     * @param mandateCallback Callback for every change
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(DocumentCallback mandateCallback) throws IOException, TwikeyClient.UserException {
        URL myurl = twikeyClient.getUrl("/mandate");
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

                    JSONArray messagesArr = json.getJSONArray("Messages");
                    isEmpty = messagesArr.isEmpty();
                    if (!isEmpty) {
                        for (int i = 0; i < messagesArr.length(); i++) {
                            JSONObject obj = messagesArr.getJSONObject(i);
                            if (obj.has("CxlRsn")) {
                                mandateCallback.cancelledDocument(obj);
                            } else if (obj.has("AmdmntRsn")) {
                                mandateCallback.updatedDocument(obj);
                            } else {
                                mandateCallback.newDocument(obj);
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
}
