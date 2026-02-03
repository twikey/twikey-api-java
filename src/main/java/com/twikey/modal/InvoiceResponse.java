package com.twikey.modal;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public interface InvoiceResponse {
    /**
     * Invoice represents the response from the Twikey API when an invoice is created.
     */
    class Invoice {

        private String id;
        private String number;
        private String title;
        private String remittance;
        private String ref;
        private Integer ct;
        private Double amount;
        private String date;
        private String duedate;
        private String state;
        private String url;

        // Optional / Extended fields
        private String lastpayment;
        private JSONObject meta;
        private JSONObject customer;

        /**
         * Factory method to build an Invoice object from JSON.
         *
         * @param json JSON object containing the invoice fields.
         * @return Invoice instance populated with API response values.
         */
        public static Invoice fromJson(JSONObject json) {
            Invoice invoice = new Invoice();

            invoice.id = json.optString("id");
            invoice.number = json.optString("number");
            invoice.title = json.optString("title");
            invoice.remittance = json.optString("remittance");
            invoice.ref = json.optString("ref");
            invoice.ct = json.has("ct") ? json.getInt("ct") : null;
            invoice.amount = json.has("amount") ? json.getDouble("amount") : null;
            invoice.date = json.optString("date");
            invoice.duedate = json.optString("duedate");
            invoice.state = json.optString("state");
            invoice.url = json.optString("url");

            // Optional fields
            invoice.lastpayment = json.optString("lastpayment", null);
            invoice.meta = json.has("meta") ? json.getJSONObject("meta") : null;
            invoice.customer = json.has("customer") ? json.getJSONObject("customer") : null;

            return invoice;
        }

        // --- Getters ---
        public String getId() {
            return id;
        }

        public String getNumber() {
            return number;
        }

        public String getTitle() {
            return title;
        }

        public String getRemittance() {
            return remittance;
        }

        public String getRef() {
            return ref;
        }

        public Integer getCt() {
            return ct;
        }

        public Double getAmount() {
            return amount;
        }

        public String getDate() {
            return date;
        }

        public String getDuedate() {
            return duedate;
        }

        public String getState() {
            return state;
        }

        public String getUrl() {
            return url;
        }

        public String getLastpayment() {
            return lastpayment;
        }

        public JSONObject getMeta() {
            return meta;
        }

        public JSONObject getCustomer() {
            return customer;
        }

        @Override
        public String toString() {
            return "Invoice {" +
                    "id='" + id + '\'' +
                    ", number='" + number + '\'' +
                    ", title='" + title + '\'' +
                    ", remittance='" + remittance + '\'' +
                    ", ref='" + ref + '\'' +
                    ", ct=" + ct +
                    ", amount=" + amount +
                    ", date='" + date + '\'' +
                    ", duedate='" + duedate + '\'' +
                    ", state='" + state + '\'' +
                    ", url='" + url + '\'' +
                    ", lastpayment='" + lastpayment + '\'' +
                    ", meta=" + meta +
                    ", customer=" + customer +
                    '}';
        }
    }

    /**
     * Represents the response for a single invoice in a bulk invoice details response.
     */
    record BulkInvoiceDetail(String id, Map<String, String> details) {

        public static final BulkInvoiceDetail PENDING = new BulkInvoiceDetail(null, null);

        public boolean isPending() {
            return this == PENDING;
        }

        public static BulkInvoiceDetail fromJson(String batchId, JSONArray array) {
            Map<String, String> details = new HashMap<>();
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject invoice = array.getJSONObject(i);
                    details.put(
                            invoice.optString("id"),
                            invoice.optString("status")
                    );
                }
            }
            return new BulkInvoiceDetail(batchId, details);
        }

        @Override
        public String toString() {
            return "BulkInvoiceDetail{id='%s', results='%d'}".formatted(id, details.size());
        }
    }

    /**
     * PDF class specifically for invoices
     */
    record Pdf(InputStream content, String filename) {}


    enum EventType {
        PAYMENT,
        PAYMENT_FAILURE,
        REFUND;

        public static EventType parse(String eventType) {
            if (eventType == null) {
                return null;
            }
            return switch (eventType) {
                case "payment" -> PAYMENT;
                case "payment_failure" -> PAYMENT_FAILURE;
                case "refund" -> REFUND;
                default -> null;
            };
        }
    }

    enum GatewayType {
        BANK,
        PSP;

        public static GatewayType parse(String type) {
            if (type == null) {
                return null;
            }
            return switch (type) {
                case "bank" -> BANK;
                case "psp" -> PSP;
                default -> null;
            };
        }
    }

    record Origin(
            String object, // "invoice"
            String id,
            String number,
            String ref
    ) {
        public static Origin fromJson(JSONObject origin) {
            return new Origin(
                    origin.getString("object"),
                    origin.getString("id"),
                    origin.getString("number"),
                    origin.optString("ref")
            );
        }
    }

    record Gateway(int id, String name, GatewayType type, String iban /* nullable*/) {
        public static Gateway fromJson(JSONObject gateway) {
            return new Gateway(
                    gateway.getInt("id"),
                    gateway.getString("name"),
                    GatewayType.parse(gateway.getString("type")),
                    gateway.optString("iban")
            );
        }
    }

    record EventError(
            String code,
            String description,
            String category,
            String externalCode,
            String action,
            int actionStep
    ) {
        public static EventError parse(JSONObject json) {
            if (json == null) return null;
            return new EventError(
                    json.getString("code"),
                    json.getString("description"),
                    json.getString("category"),
                    json.getString("externalCode"),
                    json.optString("action"),
                    json.optInt("actionStep")
            );
        }
    }

    record Event(
            String eventId,
            EventType eventType,
            Instant occurredAt,
            double amount,
            String currency,
            Origin origin,
            Gateway gateway,
            Map<String, Object> details,
            EventError error // nullable
    ) {
        public static Event fromJson(JSONObject json) {
            Map<String, Object> details = json.getJSONObject("details").toMap();
            return
                    new Event(
                            json.getString("eventId"),
                            EventType.parse(json.getString("eventType")),
                            Instant.parse(json.getString("occurredAt")),
                            json.getDouble("amount"),
                            json.getString("currency"),
                            Origin.fromJson(json.getJSONObject("origin")),
                            Gateway.fromJson(json.getJSONObject("gateway")),
                            details,
                            EventError.parse(json.optJSONObject("error"))
                    );
        }
    }
}