package com.twikey.callback;

import com.twikey.modal.InvoiceResponse;

public interface InvoiceCallback {
    void invoice(InvoiceResponse.Invoice updatedInvoice);
}
