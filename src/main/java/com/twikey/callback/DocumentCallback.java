package com.twikey.callback;

import org.json.JSONObject;

public interface DocumentCallback {
    void newDocument(JSONObject newDocument);

    void updatedDocument(JSONObject updatedDocument);

    void cancelledDocument(JSONObject cancelledDocument);
}
