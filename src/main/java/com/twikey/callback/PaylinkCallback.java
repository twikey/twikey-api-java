package com.twikey.callback;

import com.twikey.modal.PaylinkResponse;
import org.json.JSONObject;

public interface PaylinkCallback {
    /**
     * @deprecated Use {@link #paylink(PaylinkResponse.Paylink)} instead.
     */
    @Deprecated
    default void paylink(JSONObject paylink) {}

    default void paylink(PaylinkResponse.Paylink paylink) {}
}
