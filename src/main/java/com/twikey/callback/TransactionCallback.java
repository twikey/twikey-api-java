package com.twikey.callback;

import com.twikey.modal.TransactionResponse;

public interface TransactionCallback {
    void transaction(TransactionResponse.Transaction transaction);
}
