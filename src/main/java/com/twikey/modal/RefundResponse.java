package com.twikey.modal;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public interface RefundResponse {
    /**
     * Refund represents a single refund entry returned by the
     * {@code /creditor/transaction/refund} endpoint.
     *
     * <p>Example fields include transaction identifiers, bank account details,
     * refund amounts, messages, references, and dates.</p>
     */
    class Refund {

        private String id;
        private String iban;
        private String bic;
        private double amount;
        private String msg;
        private String place;
        private String ref;
        private String date;
        private String state;
        private String bkdate;

        // --- Getters ---
        public String getId() {
            return id;
        }

        public String getIban() {
            return iban;
        }

        public String getBic() {
            return bic;
        }

        public double getAmount() {
            return amount;
        }

        public String getMsg() {
            return msg;
        }

        public String getPlace() {
            return place;
        }

        public String getRef() {
            return ref;
        }

        public String getDate() {
            return date;
        }

        public String getState() {
            return state;
        }

        public String getBkdate() {
            return bkdate;
        }

        /**
         * Factory method to create a {@link Refund} object from JSON.
         *
         * @param json JSONObject representing a single refund entry
         * @return parsed {@link Refund} object
         */
        public static Refund fromJson(JSONObject json) {
            Refund refund = new Refund();

            refund.id = json.optString("id", null);
            refund.iban = json.optString("iban", null);
            refund.bic = json.optString("bic", null);
            refund.amount = json.optDouble("amount", 0.0);
            refund.msg = json.optString("msg", null);
            refund.place = json.optString("place", null);
            refund.ref = json.optString("ref", null);
            refund.date = json.optString("date", null);
            refund.state = json.optString("state", null);
            refund.bkdate = json.optString("bkdate", null);

            return refund;
        }

        @Override
        public String toString() {
            String sb = "Refund ID   : " + id + "\n" +
                    "IBAN        : " + iban + "\n" +
                    "BIC         : " + bic + "\n" +
                    "Amount      : " + amount + "\n" +
                    "Message     : " + msg + "\n" +
                    "Place       : " + place + "\n" +
                    "Reference   : " + ref + "\n" +
                    "Date        : " + date + "\n" +
                    "State       : " + state + "\n" +
                    "Bank Date   : " + bkdate + "\n";

            return sb;
        }
    }

    /**
     * Response class for a newly created beneficiary account.
     * Can be built from a JSON response returned by the Twikey API.
     */
    class AddBeneficiaryResponse {
        private String name;
        private String iban;
        private String bic;
        private boolean available;
        private Address address;

        /**
         * Nested address object in the response.
         */
        static class Address {
            private String street;
            private String city;
            private String zip;
            private String country;
        }

        /**
         * Builds an {@code AddBeneficiaryResponse} from a JSON object.
         *
         * @param json The JSON object returned by the API.
         * @return a populated {@code AddBeneficiaryResponse}.
         */
        public static AddBeneficiaryResponse fromJson(JSONObject json) {
            AddBeneficiaryResponse resp = new AddBeneficiaryResponse();
            resp.name = json.optString("name", null);
            resp.iban = json.optString("iban", null);
            resp.bic = json.optString("bic", null);
            resp.available = json.optBoolean("available", false);

            if (json.has("address")) {
                JSONObject addrJson = json.getJSONObject("address");
                Address addr = new Address();
                addr.street = addrJson.optString("street", null);
                addr.city = addrJson.optString("city", null);
                addr.zip = addrJson.optString("zip", null);
                addr.country = addrJson.optString("country", null);
                resp.address = addr;
            }

            return resp;
        }

        public static List<AddBeneficiaryResponse> fromQuery(JSONObject json) {
            JSONArray entries = json.getJSONArray("beneficiaries");
            List<AddBeneficiaryResponse> resps = new ArrayList<>();
            for (Object entry : entries) {
                JSONObject entryJson = (JSONObject) entry;
                resps.add(AddBeneficiaryResponse.fromJson(entryJson));
            }
            return resps;
        }

        public String getName() {
            return name;
        }

        public Boolean getAvailable() {
            return available;
        }

        public String getStreet() {
            return address.street;
        }

        public String getCity() {
            return address.city;
        }

        public String getZip() {
            return address.zip;
        }

        public String getCountry() {
            return address.country;
        }

        public String getIban() {
            return iban;
        }

        public String getBic() {
            return bic;
        }


        @Override
        public String toString() {
            return "beneficiary{" +
                    "id='" + name + '\'' +
                    (iban != null ? ", iban='" + iban + '\'' : "") +
                    (bic != null ? ", bic='" + bic + '\'' : "") +
                    '}';
        }

    }

    /**
     * Response class for a credit transfer entry.
     * Can be built from a JSON object returned by the Twikey API.
     */
    class CreditTransferResponse {
        private int id;
        private String pmtinfid;
        private String progress;
        private int entries;

        /**
         * Builds a {@code CreditTransferResponse} from a JSON object.
         *
         * @param json The JSON object returned by the API.
         * @return a populated {@code CreditTransferResponse}.
         */
        public static CreditTransferResponse fromJson(JSONObject json) {
            CreditTransferResponse resp = new CreditTransferResponse();
            resp.id = json.optInt("id", -1);
            resp.pmtinfid = json.optString("pmtinfid", null);
            resp.progress = json.optString("progress", null);
            resp.entries = json.optInt("entries", 0);
            return resp;
        }

        public int getId() {
            return id;
        }

        public String getPmtinfid() {
            return pmtinfid;
        }

        public String getProgress() {
            return progress;
        }

        public int getEntries() {
            return entries;
        }

        @Override
        public String toString() {
            return "CreditTransfer{" +
                    "id=" + id +
                    (pmtinfid != null ? ", pmtinfid='" + pmtinfid + '\'' : "") +
                    (progress != null ? ", progress='" + progress + '\'' : "") +
                    ", entries=" + entries +
                    '}';
        }
    }
}