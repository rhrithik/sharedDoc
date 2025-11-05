package org.hrithik.documenteditor.schemas;

import java.util.List;

public class MessageSchema {
    private String documentId;
    private String message;
    private List<String[]> documentIds;
    private String action;
    private String access;
    private String username;

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MessageSchema() {
    }

    public MessageSchema(String action, String documentId, String message, String access) {
        this.action = action;
        this.documentId = documentId;
        this.message = message;
        this.access=access;
    }
    public MessageSchema(String action, String documentId, String message, String access, String username) {
        this.action = action;
        this.documentId = documentId;
        this.message = message;
        this.access=access;
        this.username=username;
    }

//    public MessageSchema(String action, String documentId, String message,String access) {
//        this.action = action;
//        this.documentId = documentId;
//        this.message = message;
//        this.access=access;
//    }

    public MessageSchema(String documentId, String message) {
        this.documentId = documentId;
        this.message = message;
    }

    public MessageSchema(String action, List<String[]> documentIds) {
        this.action = action;
        this.documentIds = documentIds;
    }
    public MessageSchema(String action){
        this.action=action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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

    public List<String[]> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<String[]> documentIds) {
        this.documentIds = documentIds;
    }
}

