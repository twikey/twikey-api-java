package com.twikey;

import com.twikey.callback.InvoiceCallback;
import com.twikey.modal.Customer;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class InvoiceGateway {

    private final TwikeyClient twikeyClient;

    protected InvoiceGateway(TwikeyClient twikeyClient) {
        this.twikeyClient = twikeyClient;
    }

    /**
     * @param ct             Template to use can be found @ https://www.twikey.com/r/admin#/c/template
     * @param customer       Customer details
     * @param invoiceDetails Details specific to the invoice
     * @return jsonobject <pre>{
     *                       "id": "fec44175-b4fe-414c-92aa-9d0a7dd0dbf2",
     *                       "number": "Inv20200001",
     *                       "title": "Invoice July",
     *                       "ct": 1988,
     *                       "amount": "100.00",
     *                       "date": "2020-01-31",
     *                       "duedate": "2020-02-28",
     *                       "status": "BOOKED",
     *                       "url": "https://yourpage.beta.twikey.com/invoice.html?fec44175-b4fe-414c-92aa-9d0a7dd0dbf2"
     *                   }</pre>
     * @throws IOException   When no connection could be made
     * @throws com.twikey.TwikeyClient.UserException When Twikey returns a user error (400)
     */
    public JSONObject create(long ct, Customer customer, Map<String, String> invoiceDetails) throws IOException, TwikeyClient.UserException {

        JSONObject customerAsJson = new JSONObject()
                .put("customerNumber", customer.getNumber())
                .put("email", customer.getEmail())
                .put("firstname", customer.getFirstname())
                .put("lastname", customer.getLastname())
                .put("l", customer.getLang())
                .put("address", customer.getStreet())
                .put("city", customer.getCity())
                .put("zip", customer.getZip())
                .put("country", customer.getCountry())
                .put("mobile", customer.getMobile());

        if (customer.getCompanyName() != null) {
            customerAsJson.put("companyName", customer.getCompanyName())
                    .put("coc", customer.getCoc());
        }

        JSONObject invoice = new JSONObject()
                .put("customer", customerAsJson)
                .put("date", invoiceDetails.getOrDefault("date", LocalDate.now().toString()))
                .put("duedate", invoiceDetails.getOrDefault("duedate", LocalDate.now().plusMonths(1).toString()))
                .put("ct", ct);

        for (Map.Entry<String, String> entry : invoiceDetails.entrySet()) {
            invoice.put(entry.getKey(), entry.getValue());
        }

        URL myurl = twikeyClient.getUrl("/invoice");
        HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("User-Agent", twikeyClient.getUserAgent());
        con.setRequestProperty("Authorization", twikeyClient.getSessionToken());
        con.setDoOutput(true);
        con.setDoInput(true);

        try (DataOutputStream output = new DataOutputStream(con.getOutputStream())) {
            output.writeBytes(invoice.toString());
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
     * Get updates about all invoices and reset the feed to earlier point.
     *
     * @param callback Callback for every change
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(final InvoiceCallback callback, final LocalDateTime resetToDate) throws IOException, TwikeyClient.UserException {
        HttpURLConnection con = getConnectionForFeed(resetToDate);
        processOutput(con, callback);
    }

    /**
     * Get updates about all invoices
     *
     * @param invoiceCallback Callback for every change
     * @throws IOException                When a network issue happened
     * @throws TwikeyClient.UserException When there was an issue while retrieving the mandates (eg. invalid apikey)
     */
    public void feed(final InvoiceCallback invoiceCallback) throws IOException, TwikeyClient.UserException {
        HttpURLConnection con = getConnectionForFeed(null);
        processOutput(con, invoiceCallback);
    }

    private void processOutput(final HttpURLConnection con, final InvoiceCallback callback) throws IOException, TwikeyClient.UserException {
        boolean isEmpty;
        do {

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    JSONObject json = new JSONObject(new JSONTokener(br));

                    JSONArray invoicesArr = json.getJSONArray("Invoices");
                    isEmpty = invoicesArr.isEmpty();
                    if (!invoicesArr.isEmpty()) {
                        for (int i = 0; i < invoicesArr.length(); i++) {
                            JSONObject obj = invoicesArr.getJSONObject(i);
                            callback.invoice(obj);
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
        URL myurl = twikeyClient.getUrl("/invoice");
        HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("User-Agent", twikeyClient.getUserAgent());
        con.setRequestProperty("Authorization", twikeyClient.getSessionToken());
        if(resetToDate != null) con.setRequestProperty(TwikeyClient.X_RESET, TwikeyClient.formatResetAndSetToUTC(resetToDate));
        return con;
    }
}
