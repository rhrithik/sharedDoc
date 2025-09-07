package org.hrithik.documenteditor.schemas;

public class MessageSchema {
    private String documentId;
    private String message;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageSchema(String documentId, String message) {
        this.documentId = documentId;
        this.message = message;
    }
}
