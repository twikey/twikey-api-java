package com.twikey.callback;

import com.twikey.modal.DocumentResponse;

public interface DocumentCallback {
    void newDocument(DocumentResponse.Document newDocument, String evt_time);

    void updatedDocument(DocumentResponse.Document updatedDocument, String updatedDocumentId, String reason, String author, String evt_time);

    void cancelledDocument(String cancelledDocumentNumber, String reason, String author, String evt_time);
}
