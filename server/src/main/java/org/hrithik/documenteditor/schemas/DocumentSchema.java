package org.hrithik.documenteditor.schemas;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document(collection = "documents")
public class DocumentSchema {

    @Id
    private String id;
    private String content;
    private HashMap<String, String> userAccess = new HashMap<>();

    public DocumentSchema() {
    }

    public DocumentSchema(String id, String content, String ownerUsername) {
        this.id = id;
        this.content = content;
        userAccess.put(ownerUsername,"OWNER");
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public HashMap<String, String> getUserAccess() { return userAccess; }
    public void setUserAccess(HashMap<String, String> userAccess) { this.userAccess = userAccess; }
}
