package com.twikey.modal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.twikey.modal.DocumentRequests.InviteRequest.putIfNotNull;

public interface DocumentRequests {

    /**
     * Account represents a debtor account of that mandate, that is to be created
     *
     * <p>Attributes:</p>
     *
     * <ul>
     *   <li>iban (String): IBAN of the customer.</li>
     *   <li>bic (String): BIC of the bank.</li>
     * </ul>
     */
    record Account(String iban, String bic) {
    }

    /**
     * Customer represents a person or company for whom a mandate or invitation
     * will be created in Twikey.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>lastname (String): Last name of the customer.</li>
     *   <li>firstname (String): First name of the customer.</li>
     *   <li>email (String): Email address.</li>
     *   <li>lang (String): Preferred language code (e.g., "nl", "fr", "en").</li>
     *   <li>mobile (String): Mobile phone number in international format.</li>
     *   <li>street (String): Street and house number.</li>
     *   <li>city (String): City name.</li>
     *   <li>zip (String): Postal code.</li>
     *   <li>country (String): ISO country code (e.g., "BE").</li>
     *   <li>customerNumber (String): Internal customer reference ID.</li>
     *   <li>companyName (String): Company name (if applicable).</li>
     *   <li>coc (String): Company registration number.</li>
     * </ul>
     */
    class Customer {
        private String lastname, firstname, email, lang, mobile, street, city, zip, country, customerNumber, companyName, coc;

        public Customer() {
        }

        public Customer setLastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public Customer setFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Customer setEmail(String email) {
            this.email = email;
            return this;
        }

        public Customer setLang(String lang) {
            this.lang = lang;
            return this;
        }

        public Customer setMobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public Customer setStreet(String street) {
            this.street = street;
            return this;
        }

        public Customer setCity(String city) {
            this.city = city;
            return this;
        }

        public Customer setZip(String zip) {
            this.zip = zip;
            return this;
        }

        public Customer setCountry(String country) {
            this.country = country;
            return this;
        }

        public Customer setNumber(String ref) {
            this.customerNumber = ref;
            return this;
        }

        public Customer setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Customer setCoc(String coc) {
            this.coc = coc;
            return this;
        }

        public String getLastname() {
            return lastname;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getEmail() {
            return email;
        }

        public String getLang() {
            return lang;
        }

        public String getMobile() {
            return mobile;
        }

        public String getStreet() {
            return street;
        }

        public String getCity() {
            return city;
        }

        public String getZip() {
            return zip;
        }

        public String getCountry() {
            return country;
        }

        public String getCustomerNumber() {
            return customerNumber;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getCoc() {
            return coc;
        }

        public Map<String, String> asFormParameters() {
            Map<String, String> params = new HashMap<>();
            params.put("customerNumber", getCustomerNumber());
            params.put("email", getEmail());
            params.put("firstname", getFirstname());
            params.put("lastname", getLastname());
            params.put("l", getLang());
            params.put("address", getStreet());
            params.put("city", getCity());
            params.put("zip", getZip());
            params.put("country", getCountry());
            params.put("mobile", getMobile());
            if (getCompanyName() != null) {
                params.put("companyName", getCompanyName());
                params.put("coc", getCoc());
            }
            return params;
        }
    }

    /**
     * InviteRequest holds the full set of fields that can be used
     * to initiate a mandate invitation via the Twikey API.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>ct (long): Contract template ID (required).</li>
     *   <li>mandateNumber (String): Custom mandate number.</li>
     *   <li>vatno (String): VAT number (if business).</li>
     *   <li>contractNumber (String): External contract reference.</li>
     *   <li>campaign (String): Campaign identifier.</li>
     *   <li>prefix (String): Honorific title (e.g., Mr., Ms.).</li>
     *   <li>ed (String): Execution date.</li>
     *   <li>reminderDays (Integer): Days before reminder.</li>
     *   <li>sendInvite (Boolean): Send invite immediately.</li>
     *   <li>token (String): Resume token.</li>
     *   <li>requireValidation (Boolean): Whether IBAN validation is required.</li>
     *   <li>document (String): Document reference.</li>
     *   <li>transactionAmount (Double): One-off transaction amount.</li>
     *   <li>transactionMessage (String): Transaction message.</li>
     *   <li>transactionRef (String): Transaction reference.</li>
     *   <li>plan (String): Payment plan ID.</li>
     *   <li>subscriptionStart (String): Subscription start date.</li>
     *   <li>subscriptionRecurrence (String): Recurrence rule.</li>
     *   <li>subscriptionStopAfter (Integer): Stop after N cycles.</li>
     *   <li>subscriptionAmount (Double): Subscription amount.</li>
     *   <li>subscriptionMessage (String): Subscription message.</li>
     *   <li>subscriptionRef (String): Subscription reference.</li>
     *   <li>Customer fields: lastname, firstname, email, lang, mobile, street, city, zip, country, customerNumber, companyName, coc.</li>
     * </ul>
     */
    class InviteRequest {

        private final long ct;
        private final Customer customer;
        private final Account account;
        private String l, mandateNumber, contractNumber,
                campaign, prefix, ed, token, document, transactionMessage, transactionRef,
                plan, subscriptionStart, subscriptionRecurrence, subscriptionMessage, subscriptionRef;
        private Boolean check, sendInvite, requireValidation;
        private Integer reminderDays, subscriptionStopAfter;
        private Double transactionAmount, subscriptionAmount;

        /**
         * @param ct       Template
         * @param customer Optional customer (can be null)
         * @param account  Optional account (can be null)
         */
        public InviteRequest(long ct, Customer customer, Account account) {
            this.ct = ct;
            this.customer = customer;
            this.account = account;
        }

        public InviteRequest(long ct, Customer customer) {
            this(ct, customer, null);
        }

        public InviteRequest(long ct) {
            this(ct, null, null);
        }

        /**
         * Convert this request to a flat Map<String,String> suitable for the API.
         */
        public Map<String, String> toRequest() {
            Map<String, String> result = new HashMap<>();
            result.put("ct", String.valueOf(ct));
            if (account != null) {
                putIfNotNull(result, "iban", account.iban());
                putIfNotNull(result, "bic", account.bic());
            }
            if (customer != null) {
                putIfNotNull(result, "customerNumber", customer.getCustomerNumber());
                putIfNotNull(result, "email", customer.getEmail());
                putIfNotNull(result, "firstname", customer.getFirstname());
                putIfNotNull(result, "lastname", customer.getLastname());
                putIfNotNull(result, "mobile", customer.getMobile());
                putIfNotNull(result, "address", customer.getStreet());
                putIfNotNull(result, "city", customer.getCity());
                putIfNotNull(result, "zip", customer.getZip());
                putIfNotNull(result, "country", customer.getCountry());
                putIfNotNull(result, "companyName", customer.getCompanyName());
                putIfNotNull(result, "vatno", customer.getCoc());
            }
            putIfNotNull(result, "l", l);
            putIfNotNull(result, "mandateNumber", mandateNumber);
            putIfNotNull(result, "contractNumber", contractNumber);
            putIfNotNull(result, "campaign", campaign);
            putIfNotNull(result, "prefix", prefix);
            putIfNotNull(result, "ed", ed);
            putIfNotNull(result, "token", token);
            putIfNotNull(result, "document", document);
            putIfNotNull(result, "transactionMessage", transactionMessage);
            putIfNotNull(result, "transactionRef", transactionRef);
            putIfNotNull(result, "plan", plan);
            if (subscriptionStart != null) {
                putIfNotNull(result, "subscriptionStart", subscriptionStart);
                putIfNotNull(result, "subscriptionRecurrence", subscriptionRecurrence);
                putIfNotNull(result, "subscriptionMessage", subscriptionMessage);
                putIfNotNull(result, "subscriptionRef", subscriptionRef);
                putIfNotNull(result, "subscriptionAmount", subscriptionAmount);
                putIfNotNull(result, "subscriptionStopAfter", subscriptionStopAfter);
            }
            putIfNotNull(result, "check", check);
            putIfNotNull(result, "sendInvite", sendInvite);
            putIfNotNull(result, "requireValidation", requireValidation);
            putIfNotNull(result, "reminderDays", reminderDays);
            putIfNotNull(result, "transactionAmount", transactionAmount);

            return result;
        }

        public static void putIfNotNull(Map<String, String> map, String key, Object value) {
            if (value != null) {
                if (value instanceof Boolean) {
                    map.put(key, ((Boolean) value) ? "true" : "false");
                } else {
                    map.put(key, String.valueOf(value));
                }
            }
        }

        private final static Set<String> acceptedLanguages = Set.of("nl", "fr", "en", "pt", "es", "it");

        public InviteRequest setLang(String language) {
            if (acceptedLanguages.contains(language)) {
                this.l = language;
            }
            return this;
        }

        public InviteRequest setMandateNumber(String mandateNumber) {
            this.mandateNumber = mandateNumber;
            return this;
        }

        public InviteRequest setContractNumber(String contractNumber) {
            this.contractNumber = contractNumber;
            return this;
        }

        public InviteRequest setCampaign(String campaign) {
            this.campaign = campaign;
            return this;
        }

        public InviteRequest setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public InviteRequest setExpiryd(long epoch) {
            this.ed = ed;
            return this;
        }

        public InviteRequest setToken(String token) {
            this.token = token;
            return this;
        }

        public InviteRequest setDocument(String document) {
            this.document = document;
            return this;
        }

        public InviteRequest setTransactionMessage(String transactionMessage) {
            this.transactionMessage = transactionMessage;
            return this;
        }

        public InviteRequest setTransactionRef(String transactionRef) {
            this.transactionRef = transactionRef;
            return this;
        }

        public InviteRequest setSubscriptionStart(String subscriptionStart) {
            this.subscriptionStart = subscriptionStart;
            return this;
        }

        public InviteRequest setSubscriptionRecurrence(String subscriptionRecurrence) {
            this.subscriptionRecurrence = subscriptionRecurrence;
            return this;
        }

        public InviteRequest setSubscriptionMessage(String subscriptionMessage) {
            this.subscriptionMessage = subscriptionMessage;
            return this;
        }

        public InviteRequest setSubscriptionRef(String subscriptionRef) {
            this.subscriptionRef = subscriptionRef;
            return this;
        }

        public InviteRequest setForceCheck(Boolean check) {
            this.check = check;
            return this;
        }

        public InviteRequest setRequireValidation(Boolean requireValidation) {
            this.requireValidation = requireValidation;
            return this;
        }

        public InviteRequest setReminderDays(Integer reminderDays) {
            this.reminderDays = reminderDays;
            return this;
        }

        public InviteRequest setSubscriptionStopAfter(Integer subscriptionStopAfter) {
            this.subscriptionStopAfter = subscriptionStopAfter;
            return this;
        }

        public InviteRequest setTransactionAmount(Double transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public InviteRequest setSubscriptionAmount(Double subscriptionAmount) {
            this.subscriptionAmount = subscriptionAmount;
            return this;
        }
    }

    /**
     * Request object for signing a mandate via the Twikey API.
     * <p>
     * This extends {@link InviteRequest} and adds the parameters required
     * for the <code>/creditor/sign</code> endpoint. The sign method can vary,
     * and depending on it, some additional fields become required:
     *
     * <ul>
     *   <li><b>SMS</b>: requires the customer's mobile number</li>
     *   <li><b>DIGISIGN</b>: requires {@link #digsig} (a base64-encoded PNG of a wet signature)</li>
     *   <li><b>IMPORT</b>: requires {@link #signDate}. For B2B, {@link #bankSignature} can be set to false
     *       if bank validation should be skipped.</li>
     *   <li><b>ITSME</b>: initiates an itsme signing flow via redirect {@code url}</li>
     *   <li><b>EMACHTIGING / IDIN</b>: requires BIC to be provided via the {@link Account}</li>
     *   <li><b>PAPER</b>: allows previewing or sending a paper invite. Requires template options enabled.</li>
     * </ul>
     *
     * <p>Common optional fields include the signing place, signing date,
     * and mandate reference (see {@link InviteRequest}).</p>
     */
    class SignRequest {

        private final InviteRequest invite;
        private final SignMethod method;
        private String digsig;
        private String key;
        private String signDate;
        private String place;
        private Boolean bankSignature = true; // default true

        public enum SignMethod {
            SMS("sms"),
            DIGISIGN("digisign"),
            IMPORT("import"),
            ITSME("itsme"),
            EMACHTIGING("emachtiging"),
            PAPER("paper"),
            IDIN("iDIN");

            private final String value;

            SignMethod(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }

        public SignRequest(InviteRequest invite, SignMethod method) {
            this.invite = invite;
            this.method = method;
        }

        public SignRequest setDigsig(String digsig) {
            this.digsig = digsig;
            return this;
        }

        public SignRequest setKey(String key) {
            this.key = key;
            return this;
        }

        public SignRequest setSignDate(String signDate) {
            this.signDate = signDate;
            return this;
        }

        public SignRequest setPlace(String place) {
            this.place = place;
            return this;
        }

        public SignRequest setBankSignature(Boolean bankSignature) {
            this.bankSignature = bankSignature;
            return this;
        }

        public Map<String, String> toRequest() {
            Map<String, String> result = invite.toRequest();
            putIfNotNull(result, "method", method.getValue());
            putIfNotNull(result, "digsig", digsig);
            putIfNotNull(result, "key", key);
            putIfNotNull(result, "signDate", signDate);
            putIfNotNull(result, "place", place);
            putIfNotNull(result, "bankSignature", bankSignature);
            return result;
        }
    }

    /**
     * MandateActionRequest represents a request to perform an action on an existing mandate
     * via the Twikey API.
     *
     * <p>The Twikey <code>/mandate/{mndtId}/action</code> endpoint allows you to
     * trigger workflow-related actions such as resending an invitation, sending reminders,
     * granting customer access, or toggling B2B validation checks.</p>
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li><b>mandateNumber (String)</b>: The unique identifier (<code>mndtId</code>) of the mandate
     *       on which the action will be performed (required).</li>
     *   <li><b>type (MandateActionType)</b>: The type of action to perform (required).
     *       Supported values:
     *       <ul>
     *         <li><code>INVITE</code>: Send a new mandate invitation email.</li>
     *         <li><code>REMINDER</code>: Send a reminder email for an unsigned mandate.</li>
     *         <li><code>ACCESS</code>: Grant the customer access to their mandate in the portal.</li>
     *         <li><code>AUTOMATIC_CHECK</code>: Enable automatic validation for B2B mandates.</li>
     *         <li><code>MANUAL_CHECK</code>: Disable automatic validation for B2B mandates (manual only).</li>
     *       </ul>
     *   </li>
     *   <li><b>reminder (String)</b>: Optional. Only used when <code>type=REMINDER</code>.
     *       Specifies which reminder to send (valid values: "1", "2", "3", "4").</li>
     * </ul>
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * MandateActionRequest request = new MandateActionRequest(
     *         MandateActionType.REMINDER,
     *         "MNDT12345"
     *     ).setReminder(2);
     *
     * Map<String, String> payload = request.toRequest();
     * }</pre>
     *
     * <p>The <code>toRequest()</code> method flattens this request into a
     * <code>Map&lt;String,String&gt;</code> suitable for submission as query parameters
     * or form data in the Twikey API call.</p>
     *
     * <p>Endpoint Reference:</p>
     * POST https://api.twikey.com/creditor/mandate/{mndtId}/action
     */
    class MandateActionRequest {
        private final String mandateNumber;
        private final MandateActionType type;
        private String reminder;

        public enum MandateActionType {
            INVITE("invite"),
            REMINDER("reminder"),
            ACCESS("access"),
            AUTOMATIC_CHECK("automaticCheck"),
            MANUAL_CHECK("manualCheck");

            private final String value;

            MandateActionType(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }

        /**
         * @param type one of invite, reminder, access, automaticCheck, manualCheck
         */
        public MandateActionRequest(MandateActionType type, String mandateNumber) {
            this.type = type;
            this.mandateNumber = mandateNumber;
        }

        public MandateActionRequest setReminder(int reminder) {
            if (reminder < 1 || reminder > 4) {
                throw new IllegalArgumentException("Reminder must be between 1 and 4");
            }
            this.reminder = String.valueOf(reminder);
            return this;
        }

        public Map<String, String> toRequest() {
            Map<String, String> map = new HashMap<>();
            map.put("type", type.getValue());
            map.put("mndtId", mandateNumber);
            if (reminder != null) {
                map.put("reminder", reminder);
            }
            return map;
        }
    }

    /**
     * MandateQuery represents a search request for mandates in the Twikey API.
     * <p>
     * This class allows querying contracts by IBAN, customer number, or email,
     * and supports filtering by state and pagination.
     * <p>
     * At least one of iban, customerNumber, or email is required. The state
     * parameter is optional. Pagination can be controlled via the page parameter.
     * <p>
     */
    class MandateQuery {
        private String iban;
        private String customerNumber;
        private String email;
        private String state;
        private Integer page;

        private MandateQuery(String iban, String customerNuber, String email) {
            this.iban = iban;
            this.customerNumber = customerNuber;
            this.email = email;
        }

        public static MandateQuery fromIban(String iban) {
            return new MandateQuery(iban, null, null);
        }

        public static MandateQuery fromCustomerNumber(String customerNumber) {
            return new MandateQuery(null, customerNumber, null);
        }

        public static MandateQuery fromEmail(String email) {
            return new MandateQuery(email, null, null);
        }

        public MandateQuery withIban(String iban) {
            this.iban = iban;
            return this;
        }

        public MandateQuery withCustomerNumber(String customerNumber) {
            this.customerNumber = customerNumber;
            return this;
        }

        public MandateQuery withEmail(String email) {
            this.email = email;
            return this;
        }

        /**
         * Converts the query into a Map suitable for GET query parameters.
         *
         * @return Map of non-null query parameters
         */
        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>();
            putIfNotNull(params, "iban", iban);
            putIfNotNull(params, "customerNumber", customerNumber);
            putIfNotNull(params, "email", email);
            putIfNotNull(params, "state", state);
            putIfNotNull(params, "page", page);
            return params;
        }

        /**
         * Filter by contract state.
         * Possible values: SIGNED, PREPARED, CANCELLED
         */
        public MandateQuery setState(String state) {
            this.state = state;
            return this;
        }

        /**
         * Set the page number for paginated results (0-based).
         */
        public MandateQuery setPage(Integer page) {
            this.page = page;
            return this;
        }
    }

    /**
     * MandateDetailRequest represents a request to fetch details of a specific mandate in the Twikey API.
     * <p>
     * This class allows retrieving mandate details by mandate reference (mndtId).
     * Optionally, the request can include non-signed states by setting `force` to true.
     * <p>
     */
    class MandateDetailRequest {
        private final String mandateNumber;
        private Boolean force;

        public MandateDetailRequest(String mandateNumber) {
            this.mandateNumber = mandateNumber;
            this.force = false;
        }

        /**
         * Converts the detail request into a Map suitable for GET query parameters.
         *
         * @return Map of non-null query parameters
         */
        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>();
            putIfNotNull(params, "mndtId", mandateNumber);
            params.put("force", String.valueOf(force)); // always include, default false
            return params;
        }

        public MandateDetailRequest setForce(boolean force) {
            this.force = force;
            return this;
        }
    }

    /**
     * UpdateMandateRequest holds the set of fields that can be used
     * to update mandate details via the Twikey API.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>mndtId (String, required): Mandate reference.</li>
     *   <li>ct (Long): Move mandate to a different template ID (same type).</li>
     *   <li>state (String): Mandate state ("active" or "passive").</li>
     *   <li>mobile (String): Customer's mobile number (E.164 format recommended).</li>
     *   <li>iban (String): Debtor's IBAN.</li>
     *   <li>bic (String): Debtor's BIC code (auto-generated from IBAN if omitted).</li>
     *   <li>customerNumber (String): Customer number (can move mandate to another customer).</li>
     *   <li>email (String): Email address of debtor (only include if changed).</li>
     *   <li>firstname, lastname (String): Debtor's personal names.</li>
     *   <li>companyName (String): Company name (always updated on mandate, not owner).</li>
     *   <li>coc (String): Enterprise number (only if companyName also changes).</li>
     *   <li>l (String): Language on the mandate (ISO-2).</li>
     *   <li>address, city, zip, country (String): Full address block (all must be included if one is updated).</li>
     * </ul>
     *
     * <p>Notes:</p>
     * <ul>
     *   <li>Company name and language are updated on the mandate itself, not the owner.</li>
     *   <li>Mobile number and email are updated both on mandate and customer.</li>
     *   <li>For B2B mandates: iban cannot be updated once signed.</li>
     * </ul>
     */
    class UpdateMandateRequest {
        private final String mndtId;
        private final Customer customer;
        private final Account account;

        private Long ct;
        private String state, email, firstname, lastname, companyName, coc, l;
        private String address, city, zip, country;
        private String customerNumber;
        private String mobile;

        /**
         * @param mndtId   Mandate reference (required)
         * @param customer Optional customer object (can be null)
         * @param account  Optional account object (can be null)
         */
        public UpdateMandateRequest(String mndtId, Customer customer, Account account) {
            this.mndtId = mndtId;
            this.customer = customer;
            this.account = account;
        }

        public UpdateMandateRequest(String mndtId, Customer customer) {
            this(mndtId, customer, null);
        }

        public UpdateMandateRequest(String mndtId) {
            this(mndtId, null, null);
        }

        /**
         * Convert this request to a flat Map<String,String> suitable for the API.
         */
        public Map<String, String> toRequest() {
            Map<String, String> result = new HashMap<>();
            result.put("mndtId", mndtId);
            putIfNotNull(result, "ct", ct);
            putIfNotNull(result, "state", state);

            if (account != null) {
                putIfNotNull(result, "iban", account.iban());
                putIfNotNull(result, "bic", account.bic());
            }
            if (customer != null) {
                putIfNotNull(result, "customerNumber", customer.getCustomerNumber());
                putIfNotNull(result, "email", customer.getEmail());
                putIfNotNull(result, "firstname", customer.getFirstname());
                putIfNotNull(result, "lastname", customer.getLastname());
                putIfNotNull(result, "mobile", customer.getMobile());
                putIfNotNull(result, "address", customer.getStreet());
                putIfNotNull(result, "city", customer.getCity());
                putIfNotNull(result, "zip", customer.getZip());
                putIfNotNull(result, "country", customer.getCountry());
                putIfNotNull(result, "companyName", customer.getCompanyName());
                putIfNotNull(result, "coc", customer.getCoc());
            }

            putIfNotNull(result, "email", email);
            putIfNotNull(result, "firstname", firstname);
            putIfNotNull(result, "lastname", lastname);
            putIfNotNull(result, "companyName", companyName);
            putIfNotNull(result, "coc", coc);
            putIfNotNull(result, "l", l);
            putIfNotNull(result, "address", address);
            putIfNotNull(result, "city", city);
            putIfNotNull(result, "zip", zip);
            putIfNotNull(result, "country", country);
            putIfNotNull(result, "customerNumber", customerNumber);
            putIfNotNull(result, "mobile", mobile);

            return result;
        }

        public UpdateMandateRequest setCt(Long ct) {
            this.ct = ct;
            return this;
        }

        public UpdateMandateRequest setState(String state) {
            this.state = state;
            return this;
        }

        public UpdateMandateRequest setEmail(String email) {
            this.email = email;
            return this;
        }

        public UpdateMandateRequest setFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public UpdateMandateRequest setLastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public UpdateMandateRequest setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public UpdateMandateRequest setCoc(String coc) {
            this.coc = coc;
            return this;
        }

        public UpdateMandateRequest setLang(String l) {
            this.l = l;
            return this;
        }

        public UpdateMandateRequest setAddress(String address) {
            this.address = address;
            return this;
        }

        public UpdateMandateRequest setCity(String city) {
            this.city = city;
            return this;
        }

        public UpdateMandateRequest setZip(String zip) {
            this.zip = zip;
            return this;
        }

        public UpdateMandateRequest setCountry(String country) {
            this.country = country;
            return this;
        }

        public UpdateMandateRequest setCustomerNumber(String customerNumber) {
            this.customerNumber = customerNumber;
            return this;
        }

        public UpdateMandateRequest setMobile(String mobile) {
            this.mobile = mobile;
            return this;
        }
    }

    /**
     * UploadPdfRequest holds the set of fields required to upload
     * an existing mandate PDF to the Twikey API.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>mndtId (String, required): Mandate reference.</li>
     *   <li>pdfPath (String, required): Path to the PDF file on disk.</li>
     *   <li>bankSignature (Boolean, optional): Whether the bank signature is included.
     *       Defaults to true if not provided.</li>
     * </ul>
     *
     * <p>Notes:</p>
     * <ul>
     *   <li>The provided PDF will set the mandate state to "signed".</li>
     *   <li>For B2B mandates, the <code>bankSignature</code> parameter determines
     *       whether it will be offered to the bank (Twikey-affiliated banks only).</li>
     * </ul>
     */
    record UploadPdfRequest(String mndtId, String pdfPath, boolean bankSignature) {

        /**
         * @param mndtId  Mandate reference (required).
         * @param pdfPath Path to the PDF file to be uploaded (required).
         */
        public UploadPdfRequest(String mndtId, String pdfPath) {
            this(mndtId, pdfPath, false);
            if (mndtId == null || mndtId.isEmpty()) {
                throw new IllegalArgumentException("mndtId is required");
            }
            if (pdfPath == null || pdfPath.isEmpty()) {
                throw new IllegalArgumentException("pdfPath is required");
            }
        }
    }


}
