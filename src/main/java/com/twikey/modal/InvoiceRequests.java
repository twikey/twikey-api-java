package com.twikey.modal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface InvoiceRequests {
    /**
     * CreateInvoiceRequest holds the full set of fields used to create an invoice via the Twikey API.
     */
    class CreateInvoiceRequest {
        private final String number;
        private final String date;
        private final String duedate;
        private final Double amount;
        private final DocumentRequests.Customer customer;
        private String id, title, remittance, ref, locale, pdf, pdfUrl, redirectUrl,
                email, relatedInvoiceNumber, cc;
        private String ct;
        private Boolean manual;
        private List<LineItem> lines;

        public CreateInvoiceRequest(String number, Double amount, String date, String duedate, DocumentRequests.Customer customer) {
            this.number = number;
            this.amount = amount;
            this.date = date;
            this.duedate = duedate;
            this.customer = customer;
        }

        private static void putIfNotNull(JSONObject map, String key, Object value) {
            if (value != null) map.put(key, value);
        }

        // Fluent setters
        public CreateInvoiceRequest setId(String id) {
            this.id = id;
            return this;
        }

        public CreateInvoiceRequest setTitle(String title) {
            this.title = title;
            return this;
        }

        public CreateInvoiceRequest setRemittance(String remittance) {
            this.remittance = remittance;
            return this;
        }

        public CreateInvoiceRequest setRef(String ref) {
            this.ref = ref;
            return this;
        }

        public CreateInvoiceRequest setCt(String ct) {
            this.ct = ct;
            return this;
        }

        public CreateInvoiceRequest setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Control whether or not an traansaction is created provided a signed mandate is available
         *
         * @param manual when true no transaction is created to accompany the invoice
         */
        public CreateInvoiceRequest setManual(Boolean manual) {
            this.manual = manual;
            return this;
        }

        public CreateInvoiceRequest setPdf(String pdf) {
            this.pdf = pdf;
            return this;
        }

        public CreateInvoiceRequest setPdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
            return this;
        }

        public CreateInvoiceRequest setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        public CreateInvoiceRequest setEmail(String email) {
            this.email = email;
            return this;
        }

        public CreateInvoiceRequest setRelatedInvoiceNumber(String relatedInvoiceNumber) {
            this.relatedInvoiceNumber = relatedInvoiceNumber;
            return this;
        }

        public CreateInvoiceRequest setCc(String cc) {
            this.cc = cc;
            return this;
        }

        public CreateInvoiceRequest setLines(List<LineItem> lines) {
            this.lines = lines;
            return this;
        }

        /**
         * Converts this request to a Map suitable for API submission.
         */
        public JSONObject toRequest() {
            JSONObject map = new JSONObject();
            putIfNotNull(map, "id", id);
            putIfNotNull(map, "number", number);
            putIfNotNull(map, "title", title);
            putIfNotNull(map, "remittance", remittance);
            putIfNotNull(map, "ref", ref);
            putIfNotNull(map, "ct", ct);
            putIfNotNull(map, "amount", String.valueOf(amount));
            putIfNotNull(map, "date", date);
            putIfNotNull(map, "duedate", duedate);
            putIfNotNull(map, "locale", locale);
            putIfNotNull(map, "manual", String.valueOf(manual));
            putIfNotNull(map, "pdf", pdf);
            putIfNotNull(map, "pdfUrl", pdfUrl);
            putIfNotNull(map, "redirectUrl", redirectUrl);
            putIfNotNull(map, "email", email);
            putIfNotNull(map, "relatedInvoiceNumber", relatedInvoiceNumber);
            putIfNotNull(map, "cc", cc);
            if (customer != null) {
                JSONObject jsoncustomer = new JSONObject();
                putIfNotNull(jsoncustomer, "customerNumber", customer.getCustomerNumber());
                putIfNotNull(jsoncustomer, "email", customer.getEmail());
                putIfNotNull(jsoncustomer, "firstname", customer.getFirstname());
                putIfNotNull(jsoncustomer, "lastname", customer.getLastname());
                putIfNotNull(jsoncustomer, "mobile", customer.getMobile());
                putIfNotNull(jsoncustomer, "address", customer.getStreet());
                putIfNotNull(jsoncustomer, "city", customer.getCity());
                putIfNotNull(jsoncustomer, "zip", customer.getZip());
                putIfNotNull(jsoncustomer, "country", customer.getCountry());
                putIfNotNull(jsoncustomer, "companyName", customer.getCompanyName());
                putIfNotNull(jsoncustomer, "vatno", customer.getCoc());
                map.put("customer", jsoncustomer);
            }
            if (lines != null && !lines.isEmpty()) {
                List<JSONObject> lineMaps = new ArrayList<>();
                for (LineItem line : lines) lineMaps.add(line.toMap());
                map.put("lines", lineMaps.toString());
            }
            return map;
        }

        public static class LineItem {
            private String code, description, uom, vatcode;
            private Integer quantity;
            private Double unitprice, vatsum;

            public LineItem setCode(String code) {
                this.code = code;
                return this;
            }

            public LineItem setDescription(String description) {
                this.description = description;
                return this;
            }

            public LineItem setQuantity(Integer quantity) {
                this.quantity = quantity;
                return this;
            }

            public LineItem setUom(String uom) {
                this.uom = uom;
                return this;
            }

            public LineItem setUnitprice(Double unitprice) {
                this.unitprice = unitprice;
                return this;
            }

            public LineItem setVatcode(String vatcode) {
                this.vatcode = vatcode;
                return this;
            }

            public LineItem setVatsum(Double vatsum) {
                this.vatsum = vatsum;
                return this;
            }

            public JSONObject toMap() {
                JSONObject map = new JSONObject();
                putIfNotNull(map, "code", code);
                putIfNotNull(map, "description", description);
                putIfNotNull(map, "quantity", quantity);
                putIfNotNull(map, "uom", uom);
                putIfNotNull(map, "unitprice", unitprice);
                putIfNotNull(map, "vatcode", vatcode);
                putIfNotNull(map, "vatsum", vatsum);
                return map;
            }

//            private static void putIfNotNull(Map<String, Object> map, String key, Object value) {
//                if (value != null) map.put(key, value);
//            }
        }
    }

    /**
     * UpdateInvoiceRequest holds the set of fields that can be used
     * to update invoice details via the Twikey API.
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>id (String, required): Unique invoice ID (from Twikey).</li>
     *   <li>title (String): Title of the invoice.</li>
     *   <li>date (String, required): Invoice date (ISO-8601 format).</li>
     *   <li>duedate (String, required): Invoice due date (ISO-8601 format).</li>
     *   <li>ref (String): Invoice reference.</li>
     *   <li>pdf (String): Base64 encoded invoice document.</li>
     *   <li>status (String): Mark invoice as "booked", "archived" or "paid".</li>
     *   <li>extra (String): Custom attributes in JSON or string form.</li>
     * </ul>
     *
     * <p>Notes:</p>
     * <ul>
     *   <li>Updating the status to "paid" for invoices already paid will just return the invoice object in the response.</li>
     *   <li>date and duedate are mandatory fields for the update request.</li>
     *   <li>Invalid state or invalid dates will result in error codes from the API.</li>
     * </ul>
     */
    class UpdateInvoiceRequest {
        private final String id;   // Required
        private String title;
        private String date;
        private String duedate;
        private String ref;
        private String pdf;
        private String status;
        private String extra;

        /**
         * @param id Unique invoice ID (required, from Twikey API)
         */
        public UpdateInvoiceRequest(String id, String date, String duedate) {
            this.id = id;
            this.date = date;
            this.duedate = duedate;
        }

        private static void putIfNotNull(JSONObject json, String key, Object value) {
            if (value != null) json.put(key, value.toString());
        }

        /**
         * Convert this request to a flat Map<String,String> suitable for the API.
         */
        public JSONObject toRequest() {
            JSONObject result = new JSONObject();
            result.put("id", id);
            putIfNotNull(result, "title", title);
            putIfNotNull(result, "date", date);
            putIfNotNull(result, "duedate", duedate);
            putIfNotNull(result, "ref", ref);
            putIfNotNull(result, "pdf", pdf);
            putIfNotNull(result, "status", status);
            putIfNotNull(result, "extra", extra);
            return result;
        }

        // --- Builder-style setters ---
        public UpdateInvoiceRequest setTitle(String title) {
            this.title = title;
            return this;
        }

        public UpdateInvoiceRequest setDate(String date) {
            this.date = date;
            return this;
        }

        public UpdateInvoiceRequest setDuedate(String duedate) {
            this.duedate = duedate;
            return this;
        }

        public UpdateInvoiceRequest setRef(String ref) {
            this.ref = ref;
            return this;
        }

        public UpdateInvoiceRequest setPdf(String pdf) {
            this.pdf = pdf;
            return this;
        }

        public UpdateInvoiceRequest setStatus(String status) {
            this.status = status;
            return this;
        }

        public UpdateInvoiceRequest setExtra(String extra) {
            this.extra = extra;
            return this;
        }
    }

    /**
     * InvoiceDetailRequest represents a request to fetch details of a specific invoice
     * via the Twikey API.
     *
     * <p>
     * This class allows retrieving invoice details by invoice ID or invoice number.
     * Optionally, additional information can be included in the response using the
     * `include` parameter (lastpayment, meta, customer).
     * </p>
     *
     * <p>Attributes:</p>
     * <ul>
     *   <li>invoice (String, required): Invoice unique ID or invoice number.</li>
     *   <li>includeLastPayment (boolean): If true, include last payment details.</li>
     *   <li>includeMeta (boolean): If true, include meta-information about the invoice.</li>
     *   <li>includeCustomer (boolean): If true, include the full customer object.</li>
     * </ul>
     *
     * <p>Notes:</p>
     * <ul>
     *   <li>The request is rate limited (HTTP 429 if too many requests).</li>
     *   <li>Each `include` parameter is added as a separate query string entry.</li>
     *   <li>Invoice identifier can be either Twikey’s internal UUID or the invoice number.</li>
     * </ul>
     */
    class InvoiceDetailRequest {
        private final String invoice;
        private boolean includeLastPayment;
        private boolean includeMeta;
        private boolean includeCustomer;

        /**
         * @param invoice Unique invoice ID or invoice number (required).
         */
        public InvoiceDetailRequest(String invoice) {
            this.invoice = invoice;
            this.includeLastPayment = false;
            this.includeMeta = false;
            this.includeCustomer = false;
        }

        /**
         * Converts the detail request into a Map suitable for GET query parameters.
         *
         * @return Map of query parameters
         */
        public Map<String, String> toRequest() {
            Map<String, String> params = new HashMap<>();
            params.put("invoice", invoice);
            // invoice goes into the URL path, so only includes go into query params
            if (includeLastPayment) {
                params.put("include", "include=lastpayment");
            }
            if (includeMeta) {
                // allow multiple includes → append with a special format
                params.merge("include", "include=meta", (oldVal, newVal) -> oldVal + "&" + newVal);
            }
            if (includeCustomer) {
                params.merge("include", "include=customer", (oldVal, newVal) -> oldVal + "&" + newVal);
            }
            return params;
        }

        // --- Builder-style setters ---
        public InvoiceDetailRequest includeLastPayment(boolean include) {
            this.includeLastPayment = include;
            return this;
        }

        public InvoiceDetailRequest includeMeta(boolean include) {
            this.includeMeta = include;
            return this;
        }

        public InvoiceDetailRequest includeCustomer(boolean include) {
            this.includeCustomer = include;
            return this;
        }

        public String getInvoice() {
            return invoice;
        }
    }

    /**
     * Represents an action that can be performed on an invoice.
     *
     * <p>There are two main types of actions:
     * <ul>
     *   <li><b>Simple action</b>: Any generic invoice action like "CANCEL", "REMINDER", etc.</li>
     *   <li><b>Payment plan</b>: A structured plan with an initial payment and recurring monthly payments.</li>
     * </ul>
     */
    class InvoiceActionRequest {
        private final String id;
        private final String type;

        // Payment plan specific fields
        private final Double initialAmount;
        private final Double recurringAmount;
        private final Integer terms;
        private final String mndtId;

        // Private constructor: ensures object creation only through factories
        private InvoiceActionRequest(String id, String type, Double initialAmount, Double recurringAmount, Integer terms, String mndtId) {
            this.id = id;
            this.type = type;
            this.initialAmount = initialAmount;
            this.recurringAmount = recurringAmount;
            this.terms = terms;
            this.mndtId = mndtId;
        }

        // Factory method for simple invoice actions
        public static InvoiceActionRequest simple(String id, String type) {
            return new InvoiceActionRequest(id, type, null, null, null, null);
        }

        // Factory method for payment plan
        public static InvoiceActionRequest paymentPlan(String id, double initialAmount, double recurringAmount, int terms, String mndtId) {
            return new InvoiceActionRequest(id, "paymentplan", initialAmount, recurringAmount, terms, mndtId);
        }

        private static void putIfNotNull(Map<String, String> map, String key, Object value) {
            if (value != null) map.put(key, String.valueOf(value));
        }

        public Map<String, String> toRequest() {
            Map<String, String> map = new HashMap<>();
            putIfNotNull(map, "id", id);
            putIfNotNull(map, "type", type);
            putIfNotNull(map, "initialAmount", initialAmount);
            putIfNotNull(map, "recurringAmount", recurringAmount);
            putIfNotNull(map, "terms", terms);
            putIfNotNull(map, "mndtId", mndtId);
            return map;
        }
    }

    /**
     * UblUploadRequest represents the data needed to upload a UBL invoice to Twikey.
     *
     * <p>Required:</p>
     * <ul>
     *   <li>{@code xmlPath} - the path to the UBL invoice XML file.</li>
     * </ul>
     *
     * <p>Optional:</p>
     * <ul>
     *   <li>{@code manual} - if true, disables automatic collection (sets {@code X-MANUAL: true}).</li>
     *   <li>{@code invoiceId} - custom UUID for the invoice (sets {@code X-INVOICE-ID}).</li>
     * </ul>
     */
    class UblUploadRequest {

        private final String xmlPath;
        private boolean manual;
        private String invoiceId;

        /**
         * Creates a new UblUploadRequest.
         *
         * @param xmlPath the path to the UBL XML file (required)
         */
        public UblUploadRequest(String xmlPath) {
            if (xmlPath == null || xmlPath.isEmpty()) {
                throw new IllegalArgumentException("xmlPath is required");
            }
            this.xmlPath = xmlPath;
        }

        /**
         * @return the path to the UBL XML file.
         */
        public String getXmlPath() {
            return xmlPath;
        }

        /**
         * @return whether manual mode is enabled.
         */
        public boolean isManual() {
            return manual;
        }

        public UblUploadRequest setManual(Boolean manual) {
            this.manual = manual;
            return this;
        }

        /**
         * @return the optional custom invoice ID.
         */
        public String getInvoiceId() {
            return invoiceId;
        }

        public UblUploadRequest setInvoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        /**
         * Builds the HTTP headers required for this request.
         *
         * @return a map of headers for the UBL upload request
         */
        public Map<String, String> toHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/xml");
            if (manual) {
                headers.put("X-MANUAL", "true");
            }
            if (invoiceId != null && !invoiceId.isEmpty()) {
                headers.put("X-INVOICE-ID", invoiceId);
            }
            return headers;
        }
    }

    /**
     * BulkInvoiceRequest represents a batch of invoices to be created in a single API call
     * via the Twikey Bulk Create Invoices endpoint.
     *
     * <p>This request allows you to create up to 5,000 invoices in one request. Each
     * invoice is represented by a {@link CreateInvoiceRequest} object. The API will return
     * a batch ID which can be used to query the status of the bulk operation.</p>
     * <p>
     * Example usage:
     * <pre>{@code
     * List<CreateInvoiceRequest> invoices = List.of(
     *     new CreateInvoiceRequest("INV-001", 100.0, "2025-08-01", "2025-09-01", customer),
     *     new CreateInvoiceRequest("INV-002", 200.0, "2025-08-05", "2025-09-05", customer)
     * );
     *
     * BulkInvoiceRequest bulk = new BulkInvoiceRequest(invoices);
     * JSONArray body = bulk.toRequest();
     * }</pre>
     */
    record BulkInvoiceRequest(List<CreateInvoiceRequest> invoices) {

        /**
         * Constructs a BulkInvoiceRequest with the given list of invoices.
         *
         * @param invoices List of {@link CreateInvoiceRequest} objects (max 5000).
         */
        public BulkInvoiceRequest {
        }

        /**
         * Converts this bulk invoice request to a JSON array suitable for API submission.
         *
         * @return JSONArray representing the list of invoice requests.
         */
        public JSONArray toRequest() {
            JSONArray array = new JSONArray();
            for (CreateInvoiceRequest invoice : invoices) {
                array.put(invoice.toRequest());
            }
            return array;
        }
    }

}