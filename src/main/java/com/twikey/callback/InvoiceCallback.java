package com.twikey.callback;

import org.json.JSONObject;

public interface InvoiceCallback {
    void invoice(JSONObject updatedInvoice);
}
