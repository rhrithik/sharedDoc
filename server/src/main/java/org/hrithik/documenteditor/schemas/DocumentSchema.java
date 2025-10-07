package org.hrithik.documenteditor.schemas;


import org.hrithik.documenteditor.enums.accessEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.HashSet;

@Document(collection = "documents")
public class DocumentSchema {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

//    public HashMap<String, accessEnum> getUserAccess() {
//        return userAccess;
//    }

//    public void setUserAccess(HashMap<String, accessEnum> userAccess) {
//        this.userAccess = userAccess;
//    }

    @Id
    private String id;
    private String content;
//    private HashMap<String, accessEnum> userAccess;
    public DocumentSchema() {}

    public DocumentSchema(String id, String content) {
        this.id = id;
        this.content = content;
//        userAccess = new HashMap<>();
    }

}
