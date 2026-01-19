package com.twikey.modal;

import org.json.JSONObject;

import java.util.Map;

public class RequestUtils {

    public static void putIfNotNull(Map<String, String> map, String key, Object value) {
        if (value != null) {
            String val = String.valueOf(value);
            if (!val.isBlank()) {
                map.put(key, val);
            }
        }
    }

    public static void putIfNotNull(JSONObject json, String key, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isBlank()) {
                return;
            }
            json.put(key, value);
        }
    }
}
