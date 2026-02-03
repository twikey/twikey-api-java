package com.twikey.callback;

import com.twikey.modal.InvoiceResponse;

public interface PaymentCallback {
    /**
     * Callback with one payment event at a time
     *
     * @param payment event
     */
    void payment(InvoiceResponse.Event payment);
}
