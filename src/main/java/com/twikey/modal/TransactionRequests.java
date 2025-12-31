package com.twikey.modal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TransactionRequests {
    /**
     * This request class is used to construct a payload for creating a new transaction
     * tied to an existing mandate. The transaction can include amount, date, and
     * additional metadata such as message and references.
     * <p>
     * Attributes:
     * <ul>
     *   <li>mandateNumber (String): Mandate reference (required).</li>
     *   <li>date (String): Transaction date in ISO format YYYY-MM-DD (optional).</li>
     *   <li>reqcolldt (String): Desired collection date (optional).</li>
     *   <li>message (String): Message on bank statement (required, max. 140 chars).</li>
     *   <li>ref (String): Internal reference (optional).</li>
     *   <li>amount (Double): Amount to be collected (required).</li>
     *   <li>place (String): Place of transaction (optional).</li>
     *   <li>refase2e (Boolean): Use ref as E2E ID (optional).</li>
     * </ul>
     */
    class NewTransactionRequest {

        private final String mandateNumber;
        private String date;
        private String reqcolldt;
        private final String message;
        private String ref;
        private final Double amount;
        private String place;
        private Boolean refase2e;

        /**
         * Creates a new {@code NewTransactionRequest}.
         *
         * @param mandateNumber Mandate reference (required).
         * @param message       Message on bank statement (required, max. 140 chars).
         * @param amount        Amount to be collected (required).
         */
        public NewTransactionRequest(String mandateNumber, String message, Double amount) {
            this.mandateNumber = mandateNumber;
            this.message = message;
            this.amount = amount;
        }

        public NewTransactionRequest setDate(String date) {
            this.date = date;
            return this;
        }

        public NewTransactionRequest setReqcolldt(String reqcolldt) {
            this.reqcolldt = reqcolldt;
            return this;
        }

        public NewTransactionRequest setRef(String ref) {
            this.ref = ref;
            return this;
        }

        public NewTransactionRequest setPlace(String place) {
            this.place = place;
            return this;
        }

        public NewTransactionRequest setRefase2e(Boolean refase2e) {
            this.refase2e = refase2e;
            return this;
        }

        /**
         * Converts this request object into a {@link Map} suitable for posting
         * to the Twikey API.
         *
         * @return a Map representing the request payload with the correct field names.
         */
        public Map<String, String> toRequestMap() {
            Map<String, String> payload = new HashMap<>();
            putIfNotNull(payload, "mndtId", mandateNumber);
            putIfNotNull(payload, "date", date);
            putIfNotNull(payload, "reqcolldt", reqcolldt);
            putIfNotNull(payload, "message", message);
            putIfNotNull(payload, "ref", ref);
            putIfNotNull(payload, "amount", amount);
            putIfNotNull(payload, "place", place);
            putIfNotNull(payload, "refase2e", refase2e);
            return payload;
        }

        static void putIfNotNull(Map<String, String> map, String key, Object value) {
            if (value != null) map.put(key, String.valueOf(value));
        }
    }

    /**
     * Represents a request for retrieving the status of mandates in Twikey.
     *
     * <p>This request can include optional filters such as {@code id}, {@code ref}, {@code mandateNumber},
     * and {@code state}. Additionally, you can specify one or more {@code include} fields using the
     * {@link IncludeField} enum to enrich the response with related information such as collection details,
     * last update, or links.</p>
     *
     * <p>The {@link #toParams()} method generates the query parameters in a
     * {@code Map<String, String>} format, where multiple {@code include} fields are represented
     * as repeated parameters, e.g. {@code include=collection&include=lastupdate}.</p>
     */
    class StatusRequest {
        public enum IncludeField {
            COLLECTION("collection"),
            LASTUPDATE("lastupdate"),
            LINK("link");

            private final String value;

            IncludeField(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }

        private String id;
        private String ref;
        private String mandateNumber;
        private String state;
        private List<IncludeField> includes = new ArrayList<>();

        /**
         * Empty constructor as all parameters are optional.
         */
        public StatusRequest() {
        }

        public StatusRequest addInclude(IncludeField includeField) {
            if (includeField != null) {
                this.includes.add(includeField);
            }
            return this;
        }

        // --- Setters ---
        public StatusRequest setId(String id) {
            this.id = id;
            return this;
        }

        public StatusRequest setRef(String ref) {
            this.ref = ref;
            return this;
        }

        public StatusRequest setMandateNumber(String mandateNumber) {
            this.mandateNumber = mandateNumber;
            return this;
        }

        public StatusRequest setState(String state) {
            this.state = state;
            return this;
        }

        public StatusRequest setIncludes(List<IncludeField> includes) {
            this.includes = includes;
            return this;
        }

        /**
         * Converts the object into a Map of query parameters for the GET request.
         *
         * @return Map of query parameters with correct field names for the Twikey API.
         */
        public Map<String, String> toParams() {
            Map<String, String> params = new HashMap<>();

            if (id != null) params.put("id", id);
            if (ref != null) params.put("ref", ref);
            if (mandateNumber != null) params.put("mndtId", mandateNumber);
            if (state != null) params.put("state", state);
            // Concatenate includes into the proper query string format
            return params;
        }

        public String toInclude() {
            StringBuilder sb = null;
            if (!includes.isEmpty()) {
                sb = new StringBuilder();
                for (IncludeField f : includes) {
                    sb.append("&include=").append(f.getValue());
                }
            }
            assert sb != null;
            return sb.toString();
        }
    }

    /**
     * Represents a request to update an existing transaction via the Twikey API.
     *
     * <p>This class is used to build a request body for the endpoint:
     * <b>PUT /creditor/transaction</b>.
     *
     * <p>Required field:
     * <ul>
     *   <li>{@code id} – The transaction ID.</li>
     * </ul>
     *
     * <p>Optional fields:
     * <ul>
     *   <li>{@code reqcolldt} – Requested collection date in YYYY-MM-DD format.</li>
     *   <li>{@code message} – Message to display to the customer (max 140 chars).</li>
     *   <li>{@code ref} – Internal reference string.</li>
     *   <li>{@code amount} – Amount to be billed (e.g. "10.00").</li>
     *   <li>{@code place} – Optional place of transaction.</li>
     * </ul>
     *
     * <p>Use {@link #toRequest()} to convert this object into a key-value map
     * suitable for form-urlencoded HTTP requests.
     */
    class UpdateTransactionRequest {

        private final String id;          // Required

        private String reqcolldt;         // Optional
        private String message;           // Optional
        private String ref;               // Optional
        private String amount;            // Optional
        private String place;             // Optional

        /**
         * Creates a new UpdateTransactionRequest with the required transaction ID.
         *
         * @param id the transaction ID (required, must not be null or empty)
         * @throws IllegalArgumentException if id is null or empty
         */
        public UpdateTransactionRequest(String id) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("Transaction ID (id) is required and cannot be null or empty.");
            }
            this.id = id;
        }

        // ---------- Setters for optional fields ----------

        public UpdateTransactionRequest setReqcolldt(String reqcolldt) {
            this.reqcolldt = reqcolldt;
            return this;
        }

        public UpdateTransactionRequest setMessage(String message) {
            this.message = message;
            return this;
        }

        public UpdateTransactionRequest setRef(String ref) {
            this.ref = ref;
            return this;
        }

        public UpdateTransactionRequest setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public UpdateTransactionRequest setPlace(String place) {
            this.place = place;
            return this;
        }

        /**
         * Converts this request object into a Map of key-value pairs suitable
         * for form-urlencoded HTTP request bodies.
         *
         * @return a {@code Map<String, String>} containing all non-null parameters
         */
        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>();
            params.put("id", this.id);

            if (reqcolldt != null && !reqcolldt.isBlank()) {
                params.put("reqcolldt", reqcolldt);
            }
            if (message != null && !message.isBlank()) {
                params.put("message", message);
            }
            if (ref != null && !ref.isBlank()) {
                params.put("ref", ref);
            }
            if (amount != null && !amount.isBlank()) {
                params.put("amount", amount);
            }
            if (place != null && !place.isBlank()) {
                params.put("place", place);
            }

            return params;
        }
    }

    /**
     * Represents a request to execute an action on a specific transaction in Twikey.
     *
     * <p>This class is used to build the request body for the
     * {@code POST /creditor/transaction/action} endpoint.</p>
     *
     * <p>Typical usage:</p>
     * <pre>
     *     ActionRequest request = new ActionRequest("345", ActionRequest.Action.REOFFER);
     *     Map<String, String> params = request.toRequest();
     *     // pass params into your OkHttp FormBody builder
     * </pre>
     *
     * <p>Available actions are defined in the {@link Action} enum.</p>
     */
    class ActionRequest {

        private final String id;
        private final Action action;

        /**
         * Enum representing the allowed actions that can be executed on a transaction.
         */
        public enum Action {
            PAID("paid"),
            REOFFER("reoffer"),
            BACKUP("backup"),
            UNSETTLE("unsettle"),
            ARCHIVE("archive");

            private final String value;

            Action(String value) {
                this.value = value;
            }

            /**
             * Returns the string value that should be used in the request.
             *
             * @return the action string
             */
            public String getValue() {
                return value;
            }
        }

        /**
         * Creates a new {@code ActionRequest}.
         *
         * @param id     The transaction ID (required).
         * @param action The action to execute on the transaction (required).
         */
        public ActionRequest(String id, Action action) {
            this.id = id;
            this.action = action;
        }

        /**
         * Converts this request object into a {@link Map} that can be
         * directly used as form parameters for the API request.
         *
         * @return a map of parameter names and values
         */
        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>();
            if (id != null && !id.isEmpty()) {
                params.put("id", id);
            }
            if (action != null) {
                params.put("action", action.getValue());
            }
            return params;
        }

        public String getId() {
            return id;
        }

        public Action getAction() {
            return action;
        }
    }

    /**
     * RefundRequest models a POST request to /creditor/transaction/refund
     * to refund a specific transaction.
     * <p>
     * Required parameters: id, message, amount.
     * Optional parameters: ref, place, iban, bic.
     */
    class RefundRequest {

        // --- Required fields ---
        private final String id;       // Transaction ID
        private final String message;  // Refund message
        private final double amount;   // Refund amount

        // --- Optional fields ---
        private String ref;    // Reference for the refund
        private String place;  // Place of refund
        private String iban;   // IBAN of the beneficiary account
        private String bic;    // BIC of the beneficiary account

        /**
         * Constructor for required fields.
         *
         * @param id      Transaction ID
         * @param message Refund message
         * @param amount  Amount to refund
         */
        public RefundRequest(String id, String message, double amount) {
            this.id = id;
            this.message = message;
            this.amount = amount;
        }

        // --- Setters for optional fields ---
        public RefundRequest setRef(String ref) {
            this.ref = ref;
            return this;
        }

        public RefundRequest setPlace(String place) {
            this.place = place;
            return this;
        }

        public RefundRequest setIban(String iban) {
            this.iban = iban;
            return this;
        }

        public RefundRequest setBic(String bic) {
            this.bic = bic;
            return this;
        }

        // --- Convert to request payload ---
        public Map<String, String> toRequest() {
            Map<String, String> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("message", message);
            payload.put("amount", String.valueOf(amount));

            if (ref != null && !ref.isEmpty()) payload.put("ref", ref);
            if (place != null && !place.isEmpty()) payload.put("place", place);
            if (iban != null && !iban.isEmpty()) payload.put("iban", iban);
            if (bic != null && !bic.isEmpty()) payload.put("bic", bic);

            return payload;
        }
    }

    /**
     * RemoveTransactionRequest models a DELETE request to remove a transaction
     * that hasn't yet been sent to the bank. At least one of the parameters
     * {@code id} or {@code ref} must be provided.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>{@code id} - A transaction ID as returned in the POST response. Optional.</li>
     *   <li>{@code ref} - The transaction reference provided during creation. Optional.</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>
     *     RemoveTransactionRequest req = RemoveTransactionRequest.withId("12345");
     *     Map&lt;String, String&gt; payload = req.toRequest();
     * </pre>
     */
    class RemoveTransactionRequest {

        private final String id;
        private final String ref;

        private RemoveTransactionRequest(String id, String ref) {
            this.id = id;
            this.ref = ref;
        }

        /**
         * Factory method for removing a transaction by ID only.
         *
         * @param id Transaction ID
         * @return a new {@link RemoveTransactionRequest}
         */
        public static RemoveTransactionRequest withId(String id) {
            return new RemoveTransactionRequest(id, null);
        }

        /**
         * Factory method for removing a transaction by reference only.
         *
         * @param ref Transaction reference
         * @return a new {@link RemoveTransactionRequest}
         */
        public static RemoveTransactionRequest withRef(String ref) {
            return new RemoveTransactionRequest(null, ref);
        }

        /**
         * Factory method for removing a transaction by both ID and reference.
         *
         * @param id  Transaction ID
         * @param ref Transaction reference
         * @return a new {@link RemoveTransactionRequest}
         */
        public static RemoveTransactionRequest withIdAndRef(String id, String ref) {
            return new RemoveTransactionRequest(id, ref);
        }

        /**
         * Converts this request into a map suitable for sending as
         * request parameters in the DELETE call.
         *
         * @return a map containing non-null and non-empty attributes
         */
        public Map<String, String> toRequest() {
            Map<String, String> retval = new HashMap<>();
            if (id != null && !id.isEmpty()) {
                retval.put("id", id);
            }
            if (ref != null && !ref.isEmpty()) {
                retval.put("ref", ref);
            }
            return retval;
        }

        public String getId() {
            return id;
        }

        public String getRef() {
            return ref;
        }
    }

    /**
     * QueryTransactionsRequest models a GET request to
     * {@code /creditor/transaction/query} to retrieve a list of transactions,
     * starting from a specific transaction ID.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>{@code fromId} - The ID of the transaction to start from (required).</li>
     *   <li>{@code mndtId} - The mandate reference (optional).</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>
     *     QueryTransactionsRequest req = new QueryTransactionsRequest(1000000L);
     *     req.setMndtId("EXAMPLE-AI3I");
     *     Map&lt;String, Object&gt; queryParams = req.toRequest();
     * </pre>
     */
    class QueryRequest {

        private final long fromId;   // Required
        private String mndtId;       // Optional

        /**
         * Creates a new {@link QueryRequest} with the required parameter.
         *
         * @param fromId The ID of the transaction to start from
         */
        public QueryRequest(long fromId) {
            this.fromId = fromId;
        }

        /**
         * Sets the optional mandate reference.
         *
         * @param mndtId Mandate reference
         * @return this {@link QueryRequest} (for chaining)
         */
        public QueryRequest setMndtId(String mndtId) {
            this.mndtId = mndtId;
            return this;
        }

        public long getFromId() {
            return fromId;
        }

        public String getMndtId() {
            return mndtId;
        }

        /**
         * Converts this request into a map suitable for query parameters
         * in the GET call.
         *
         * @return a map containing non-null and non-empty attributes
         */
        public Map<String, String> toRequest() {
            Map<String, String> retval = new HashMap<>();
            retval.put("fromId", String.valueOf(fromId));
            if (mndtId != null && !mndtId.isEmpty()) {
                retval.put("mndtId", mndtId);
            }
            return retval;
        }
    }
}
