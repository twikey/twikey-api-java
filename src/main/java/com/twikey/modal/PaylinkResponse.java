package com.twikey.modal;

import org.json.JSONObject;

public interface PaylinkResponse {

    record Paylink(
            long id,
            double amount,
            String message,
            String url
    ) {
        public static Paylink fromJson(JSONObject json) {
            return new Paylink(
                    json.getLong("id"),
                    json.getDouble("amount"),
                    json.optString("msg"),
                    json.getString("url")
            );
        }
    }
}
