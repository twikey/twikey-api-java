package com.twikey.modal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public interface TransactionResponse {
    /**
     * Transaction represents a single transaction entry returned by the
     * {@code /creditor/transaction/query} or related endpoints.
     *
     * <p>Example fields include transaction identifiers, mandate information,
     * amounts, states, and optional bank error messages or actions.</p>
     */
    class Transaction {

        private long id;
        private long contractId;
        private String mndtId;
        private String contract;
        private double amount;
        private Double admincharge; // optional
        private String msg;
        private String place;
        private String ref;
        private String date;
        private boolean isFinal;
        private String state;
        private String bkerror;
        private String bkmsg;
        private String bkdate;
        private Double bkamount;
        private int collection;
        private String reqcolldt;
        private String link;

        private final List<TransactionAction> actions = new ArrayList<>();

        // --- Getters ---
        public long getId() {
            return id;
        }

        public long getContractId() {
            return contractId;
        }

        public String getMndtId() {
            return mndtId;
        }

        public String getContract() {
            return contract;
        }

        public double getAmount() {
            return amount;
        }

        public Double getAdmincharge() {
            return admincharge;
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

        public boolean isFinal() {
            return isFinal;
        }

        public String getState() {
            return state;
        }

        public String getBkerror() {
            return bkerror;
        }

        public String getBkmsg() {
            return bkmsg;
        }

        public String getBkdate() {
            return bkdate;
        }

        public Double getBkamount() {
            return bkamount;
        }

        public int getCollection() {
            return collection;
        }

        public String getReqcolldt() {
            return reqcolldt;
        }

        public String getLink() {
            return link;
        }

        public List<TransactionAction> getActions() {
            return actions;
        }

        /**
         * Factory method to create a {@link Transaction} object from JSON.
         *
         * @param json JSONObject representing a single entry
         * @return parsed {@link Transaction}
         */
        public static Transaction fromJson(JSONObject json) {
            Transaction tx = new Transaction();

            tx.id = json.optLong("id");
            tx.contractId = json.optLong("contractId");
            tx.mndtId = json.optString("mndtId", null);
            tx.contract = json.optString("contract", null);
            tx.amount = json.optDouble("amount", 0.0);
            if (json.has("admincharge")) {
                tx.admincharge = json.optDouble("admincharge");
            }
            tx.msg = json.optString("msg", null);
            tx.place = json.optString("place", null);
            tx.ref = json.optString("ref", null);
            tx.date = json.optString("date", null);
            tx.isFinal = json.optBoolean("final", false);
            tx.state = json.optString("state", null);
            tx.bkerror = json.optString("bkerror", null);
            tx.bkmsg = json.optString("bkmsg", null);
            tx.bkdate = json.optString("bkdate", null);
            if (json.has("bkamount")) {
                tx.bkamount = json.optDouble("bkamount");
            }
            tx.collection = json.optInt("collection", 0);
            tx.reqcolldt = json.optString("reqcolldt", null);
            tx.link = json.optString("link", null);

            // Parse actions if present
            if (json.has("actions")) {
                JSONArray arr = json.getJSONArray("actions");
                for (Object item : arr) {
                    JSONObject obj = (JSONObject) item;
                    tx.actions.add(TransactionAction.fromJson(obj));
                }
            }

            return tx;
        }

        /**
         * Factory to parse all transaction entries from a response JSON.
         *
         * @param response the JSON response from Twikey
         * @return list of {@link Transaction} objects
         */
        public static List<Transaction> fromQuery(JSONObject response) {
            JSONArray entries = response.getJSONArray("Entries");
            List<Transaction> txs = new ArrayList<>();
            for (Object entry : entries) {
                JSONObject obj = (JSONObject) entry;
                txs.add(Transaction.fromJson(obj));
            }
            return txs;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Transaction ID   : ").append(id).append("\n");
            sb.append("Contract ID      : ").append(contractId).append("\n");
            sb.append("Mandate ID       : ").append(mndtId).append("\n");
            sb.append("Contract         : ").append(contract).append("\n");
            sb.append("Amount           : ").append(amount).append("\n");
            sb.append("Admin Charge     : ").append(admincharge).append("\n");
            sb.append("Message          : ").append(msg).append("\n");
            sb.append("Reference        : ").append(ref).append("\n");
            sb.append("Date             : ").append(date).append("\n");
            sb.append("Final            : ").append(isFinal).append("\n");
            sb.append("State            : ").append(state).append("\n");
            sb.append("BkError          : ").append(bkerror).append("\n");
            sb.append("BkMsg            : ").append(bkmsg).append("\n");
            sb.append("BkDate           : ").append(bkdate).append("\n");
            sb.append("BkAmount         : ").append(bkamount).append("\n");
            sb.append("Collection       : ").append(collection).append("\n");
            sb.append("Req Coll Dt      : ").append(reqcolldt).append("\n");
            sb.append("Link             : ").append(link).append("\n");

            sb.append("Actions:\n");
            for (TransactionAction action : actions) {
                sb.append("  - ").append(action).append("\n");
            }

            return sb.toString();
        }

        /**
         * Inner class representing an action on a transaction.
         */
        public static class TransactionAction {
            private String type;
            private String reason;
            private String action;
            private String at;

            public String getType() {
                return type;
            }

            public String getReason() {
                return reason;
            }

            public String getAction() {
                return action;
            }

            public String getAt() {
                return at;
            }

            public static TransactionAction fromJson(JSONObject json) {
                TransactionAction a = new TransactionAction();
                a.type = json.optString("type", null);
                a.reason = json.optString("reason", null);
                a.action = json.optString("action", null);
                a.at = json.optString("at", null);
                return a;
            }

            @Override
            public String toString() {
                return String.format("%s (%s) -> %s @ %s", type, reason, action, at);
            }
        }
    }

    /**
     * Represents a refund entry in the response.
     */
    class Refund {

        private String id;
        private String iban;
        private String bic;
        private Double amount;
        private String message;
        private String place;
        private String reference;
        private String date;

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

        public Double getAmount() {
            return amount;
        }

        public String getMessage() {
            return message;
        }

        public String getPlace() {
            return place;
        }

        public String getReference() {
            return reference;
        }

        public String getDate() {
            return date;
        }

        /**
         * Factory method to create a {@link Refund} object from JSON.
         *
         * @param json JSONObject representing a refund entry
         * @return parsed {@link Refund}
         */
        public static Refund fromJson(JSONObject json) {
            Refund refund = new Refund();

            refund.id = json.optString("id", null);
            refund.iban = json.optString("iban", null);
            refund.bic = json.optString("bic", null);
            if (json.has("amount")) {
                refund.amount = json.optDouble("amount");
            }
            refund.message = json.optString("msg", null);
            refund.place = json.optString("place", null);
            refund.reference = json.optString("ref", null);
            refund.date = json.optString("date", null);

            return refund;
        }

        @Override
        public String toString() {
            return "Refund{" +
                    "id='" + id + '\'' +
                    ", iban='" + iban + '\'' +
                    ", bic='" + bic + '\'' +
                    ", amount=" + amount +
                    ", message='" + message + '\'' +
                    ", place='" + place + '\'' +
                    ", reference='" + reference + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }

}