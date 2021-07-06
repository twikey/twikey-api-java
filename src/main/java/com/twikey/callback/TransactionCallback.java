package com.twikey.callback;

import org.json.JSONObject;

public interface TransactionCallback {
    void transaction(JSONObject transaction);
}
