package com.twikey.modal;


import java.util.HashMap;
import java.util.Map;

import static com.twikey.modal.RequestUtils.putIfNotNull;

public interface RefundRequests {
    /**
     * This request class is used to construct a payload for creating a new credit transfer.
     * <p>
     * Attributes:
     * <ul>
     *   <li>customerNumber (String): The customer number (strongly recommended, required).</li>
     *   <li>iban (String): IBAN of the beneficiary account (required if it can't be derived).</li>
     *   <li>message (String): Message for the creditor (required, max 140 characters).</li>
     *   <li>amount (Double): Amount to be transferred (required).</li>
     *   <li>ref (String): Optional reference for the transaction.</li>
     *   <li>date (String): Required execution date of the transaction (optional, ISO format YYYY-MM-DD).</li>
     *   <li>place (String): Optional place of transaction.</li>
     * </ul>
     */
    class NewCreditTransferRequest {

        private final String customerNumber;
        private String iban;
        private final String message;
        private final Double amount;
        private String ref;
        private String date;
        private String place;

        /**
         * Creates a new {@code NewCreditTransferRequest}.
         *
         * @param customerNumber The customer number (required).
         * @param message        Message for the creditor (required, max 140 chars).
         * @param amount         Amount to be transferred (required).
         */
        public NewCreditTransferRequest(String customerNumber, String message, Double amount) {
            this.customerNumber = customerNumber;
            this.message = message;
            this.amount = amount;
        }

        public NewCreditTransferRequest setIban(String iban) {
            this.iban = iban;
            return this;
        }

        public NewCreditTransferRequest setRef(String ref) {
            this.ref = ref;
            return this;
        }

        public NewCreditTransferRequest setDate(String date) {
            this.date = date;
            return this;
        }

        public NewCreditTransferRequest setPlace(String place) {
            this.place = place;
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
            putIfNotNull(payload, "customerNumber", customerNumber);
            putIfNotNull(payload, "iban", iban);
            putIfNotNull(payload, "message", message);
            putIfNotNull(payload, "amount", amount);
            putIfNotNull(payload, "ref", ref);
            putIfNotNull(payload, "date", date);
            putIfNotNull(payload, "place", place);
            return payload;
        }
    }

    /**
     * Request class for creating a new beneficiary account.
     * <p>
     * Attributes:
     * <ul>
     *   <li>iban (String): IBAN of the beneficiary (required).</li>
     *   <li>customerNumber (String): Customer number (optional).</li>
     *   <li>name (String): Full name of the debtor (optional, required for new customers).</li>
     *   <li>email (String): Email of the debtor (optional).</li>
     *   <li>language (String): Language of the customer (ISO 2-letter, optional).</li>
     *   <li>mobile (String): Mobile phone number in international format (optional).</li>
     *   <li>address (String): Street and number (optional, required for new customers).</li>
     *   <li>city (String): City (optional, required for new customers).</li>
     *   <li>zip (String): Zip code (optional).</li>
     *   <li>country (String): Country in ISO 2-letter format (optional, required for new customers).</li>
     *   <li>companyName (String): Company name (optional).</li>
     *   <li>vatno (String): Enterprise number (optional).</li>
     *   <li>bic (String): BIC of the beneficiary (optional).</li>
     * </ul>
     */
    class AddBeneficiaryRequest {

        private final String iban;
        private String customerNumber;
        private String name;
        private String email;
        private String language;
        private String mobile;
        private String address;
        private String city;
        private String zip;
        private String country;
        private String companyName;
        private String vatno;
        private String bic;

        /**
         * Creates a new {@code AddBeneficiaryRequest}.
         *
         * @param iban IBAN of the beneficiary (required).
         */
        public AddBeneficiaryRequest(String iban) {
            this.iban = iban;
        }

        public AddBeneficiaryRequest setCustomerNumber(String customerNumber) {
            this.customerNumber = customerNumber;
            return this;
        }

        public AddBeneficiaryRequest setName(String name) {
            this.name = name;
            return this;
        }

        public AddBeneficiaryRequest setEmail(String email) {
            this.email = email;
            return this;
        }

        public AddBeneficiaryRequest setLanguage(String language) {
            this.language = language;
            return this;
        }

        public AddBeneficiaryRequest setMobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public AddBeneficiaryRequest setAddress(String address) {
            this.address = address;
            return this;
        }

        public AddBeneficiaryRequest setCity(String city) {
            this.city = city;
            return this;
        }

        public AddBeneficiaryRequest setZip(String zip) {
            this.zip = zip;
            return this;
        }

        public AddBeneficiaryRequest setCountry(String country) {
            this.country = country;
            return this;
        }

        public AddBeneficiaryRequest setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public AddBeneficiaryRequest setVatno(String vatno) {
            this.vatno = vatno;
            return this;
        }

        public AddBeneficiaryRequest setBic(String bic) {
            this.bic = bic;
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
            putIfNotNull(payload, "iban", iban);
            putIfNotNull(payload, "customerNumber", customerNumber);
            putIfNotNull(payload, "name", name);
            putIfNotNull(payload, "email", email);
            putIfNotNull(payload, "l", language);
            putIfNotNull(payload, "mobile", mobile);
            putIfNotNull(payload, "address", address);
            putIfNotNull(payload, "city", city);
            putIfNotNull(payload, "zip", zip);
            putIfNotNull(payload, "country", country);
            putIfNotNull(payload, "companyName", companyName);
            putIfNotNull(payload, "vatno", vatno);
            putIfNotNull(payload, "bic", bic);
            return payload;
        }
    }

    class DisableBeneficiaryRequest {
        private final String iban;            // required
        private String customerNumber;  // optional

        /**
         * Constructor for DisableBeneficiaryRequest.
         *
         * @param iban The IBAN of the beneficiary account to disable (required).
         */
        public DisableBeneficiaryRequest(String iban) {
            if (iban == null || iban.isBlank()) {
                throw new IllegalArgumentException("IBAN is required and cannot be null or blank.");
            }
            this.iban = iban;
        }

        /**
         * Builds a request map for use in constructing the HTTP request.
         * The IBAN will be used as part of the URL, while the customerNumber (if present)
         * will be added as a query parameter.
         *
         * @return A map containing the request parameters.
         */
        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>();
            if (customerNumber != null && !customerNumber.isBlank()) {
                params.put("customerNumber", customerNumber);
            }
            return params;
        }

        public DisableBeneficiaryRequest setCustomerNumber(String customerNumber) {
            this.customerNumber = customerNumber;
            return this;
        }

        public String getIban() {
            return iban;
        }

        public String getCustomerNumber() {
            return customerNumber;
        }
    }

    /**
     * Represents a request to complete a credit transfer batch.
     * <p>
     * This request is used to create a batch of refunds to send to the bank.
     * The batch can be created based on the account of the recurring gateway
     * configured on an existing profile (specified via {@code ct}), or
     * another bank account (via {@code iban}) that has refunds enabled.
     * </p>
     * <p>
     * Example usage:
     * <pre>{@code
     * CompleteCreditTransferRequest request = new CompleteCreditTransferRequest("**ct_id**")
     *     .setIban("BE12345678901234");
     * Map<String, String> params = request.toRequest();
     * }</pre>
     */
    class CompleteCreditTransferRequest {

        private final String ct;     // Required
        private String iban;         // Optional

        /**
         * Constructor for {@link CompleteCreditTransferRequest}.
         *
         * @param ct The profile ID containing the originating account (required).
         * @throws IllegalArgumentException if {@code ct} is null or blank.
         */
        public CompleteCreditTransferRequest(String ct) {
            if (ct == null || ct.isBlank()) {
                throw new IllegalArgumentException("ct (profile ID) is required and cannot be null or blank.");
            }
            this.ct = ct;
        }

        /**
         * Builds a map containing the request parameters for use in the HTTP request.
         * <p>
         * Always includes the {@code ct} parameter. If {@code iban} is set, it will
         * be included as well.
         *
         * @return A map containing the request parameters.
         */
        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>();
            params.put("ct", ct);
            if (iban != null && !iban.isBlank()) {
                params.put("iban", iban);
            }
            return params;
        }

        /**
         * Sets the optional IBAN for the originating account.
         *
         * @param iban The IBAN of the originating account (optional).
         * @return This {@link CompleteCreditTransferRequest} instance for chaining.
         */
        public CompleteCreditTransferRequest setIban(String iban) {
            this.iban = iban;
            return this;
        }

        /**
         * Gets the required profile ID ({@code ct}).
         *
         * @return The profile ID.
         */
        public String getCt() {
            return ct;
        }

        /**
         * Gets the optional IBAN of the originating account.
         *
         * @return The IBAN, or {@code null} if not set.
         */
        public String getIban() {
            return iban;
        }
    }

    /**
     * CompleteCreditTransferDetailsRequest models a GET request to fetch details
     * of a specific credit transfer batch.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>{@code id} - The batch identifier (optional).</li>
     *   <li>{@code pmtinfid} - The payment information ID of the batch (optional).</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>
     *     CompleteCreditTransferDetailsRequest req = new CompleteCreditTransferDetailsRequest()
     *         .setId("123")
     *         .setPmtinfid("Twikey-20220330113125070605075");
     *     Map&lt;String, String&gt; params = req.toRequest();
     * </pre>
     */
    class CompleteCreditTransferDetailsRequest {

        private String id;        // optional
        private String pmtinfid;  // optional

        public CompleteCreditTransferDetailsRequest() {
            // empty constructor since both fields are optional
        }

        /**
         * Sets the batch ID to fetch details for.
         *
         * @param id The batch identifier
         * @return this request object for chaining
         */
        public CompleteCreditTransferDetailsRequest setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the Payment Information ID to fetch details for.
         *
         * @param pmtinfid The payment information identifier
         * @return this request object for chaining
         */
        public CompleteCreditTransferDetailsRequest setPmtinfid(String pmtinfid) {
            this.pmtinfid = pmtinfid;
            return this;
        }

        /**
         * Converts this request object into a map of query parameters
         * suitable for a GET request.
         *
         * @return a map containing non-null and non-empty query parameters
         */
        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>();
            if (id != null && !id.isBlank()) {
                params.put("id", id);
            }
            if (pmtinfid != null && !pmtinfid.isBlank()) {
                params.put("pmtinfid", pmtinfid);
            }
            return params;
        }

        // --- Getters ---
        public String getId() {
            return id;
        }

        public String getPmtinfid() {
            return pmtinfid;
        }
    }
}