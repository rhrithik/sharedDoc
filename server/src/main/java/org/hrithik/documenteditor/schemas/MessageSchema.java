package org.hrithik.documenteditor.schemas;

import java.util.List;

public class MessageSchema {
    private String action;
    private String documentId;
    private String message;
    private String access;
    private String username;
    private List<String[]> documentIds;
    private List<String> sharedDocumentList;

    private MessageSchema(String action) {
        this.action = action;
    }

    public static MessageSchema getSingleDocument(String action, String documentId, String message, String access) {
        MessageSchema schema = new MessageSchema(action);
        schema.documentId = documentId;
        schema.message = message;
        schema.access = access;
        return schema;
    }

    public static MessageSchema getSingleDocumentWithUser(String action, String documentId, String message, String access, String username) {
        MessageSchema schema = new MessageSchema(action);
        schema.documentId = documentId;
        schema.message = message;
        schema.access = access;
        schema.username = username;
        return schema;
    }

    public static MessageSchema getDocumentIdList(String action, List<String[]> documentIds) {
        MessageSchema schema = new MessageSchema(action);
        schema.documentIds = documentIds;
        return schema;
    }

    public static MessageSchema getSharedDocuments(String action, List<String> sharedDocumentList) {
        MessageSchema schema = new MessageSchema(action);
        schema.sharedDocumentList = sharedDocumentList;
        return schema;
    }

    public static MessageSchema simpleMessage(String action, String message) {

        MessageSchema schema =  new MessageSchema(action);
        schema.message=message;
        return schema;
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

    public List<String[]> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<String[]> documentIds) {
        this.documentIds = documentIds;
    }

    public List<String> getSharedDocumentList() {
        return sharedDocumentList;
    }

    public void setSharedDocumentList(List<String> sharedDocumentList) {
        this.sharedDocumentList = sharedDocumentList;
    }
}
