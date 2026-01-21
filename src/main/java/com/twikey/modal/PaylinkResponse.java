package com.twikey.modal;

import org.json.JSONObject;

public interface PaylinkResponse {

    record Paylink(
            long id,
            long templateId,
            double amount,
            String msg,
            String remittance,
            PaylinkState state,
            String url,
            Customer customer,
            Meta meta
    ) {
        public static Paylink fromJson(JSONObject json) {
            long id = json.getLong("id");
            long templateId = json.optLong("ct");
            double amount = json.getDouble("amount");
            String msg = json.optString("msg");
            String remittance = json.optString("ref");
            String stateStr = json.optString("state", null);
            PaylinkState state = PaylinkState.parse(stateStr);
            String url = json.optString("url");

            Customer customer = null;
            if (json.has("customer")) {
                JSONObject customerJson = json.getJSONObject("customer");
                customer = new Customer()
                        .setEmail(customerJson.optString("email"))
                        .setFirstname(customerJson.optString("firstname"))
                        .setLastname(customerJson.optString("lastname"))
                        .setStreet(customerJson.optString("address"))
                        .setCity(customerJson.optString("city"))
                        .setZip(customerJson.optString("zip"))
                        .setCountry(customerJson.optString("country"))
                        .setNumber(customerJson.optString("customerNumber"))
                        .setLang(customerJson.optString("l"))
                        .setMobile(customerJson.optString("mobile"))
                        .setCompanyName(customerJson.optString("companyName"))
                        .setCoc(customerJson.optString("coc"));
            }

            Meta meta = null;
            if (json.has("meta")) {
                meta = Meta.fromJson(json.getJSONObject("meta"));
            }

            return new Paylink(id, templateId, amount, msg, remittance, state, url, customer, meta);
        }
    }

    record Meta(
            boolean active,
            String method,
            String recurringId,
            String expiry,
            String type,
            String invoice,
            String sdd,
            String tx,
            String paymentMethod
    ) {
        public static Meta fromJson(JSONObject json) {
            boolean active = json.optBoolean("active");
            String method = json.optString("method", null);
            String recurringId = json.optString("recurringId", null);
            String expiry = json.optString("expiry", null);
            String type = json.optString("type", null);
            String invoice = json.optString("invoice", null);
            String sdd = json.optString("sdd", null);
            String tx = json.optString("tx", null);
            String paymentMethod = json.optString("paymentMethod", null);

            return new Meta(active, method, recurringId, expiry, type, invoice, sdd, tx, paymentMethod);
        }
    }


    enum PaylinkState {
        CREATED("created"),
        STARTED("started"),
        PENDING("pending"),
        DECLINED("declined"),
        PAID("paid"),
        EXPIRED("expired"),
        REFUNDED("refunded");

        private final String id;

        PaylinkState(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public static PaylinkState parse(String state) {
            if (state == null) return PENDING;
            return switch (state) {
                case "created" -> CREATED;
                case "started" -> STARTED;
                case "declined" -> DECLINED;
                case "paid" -> PAID;
                case "expired" -> EXPIRED;
                case "refunded" -> REFUNDED;
                default -> PENDING;
            };
        }
    }
}
