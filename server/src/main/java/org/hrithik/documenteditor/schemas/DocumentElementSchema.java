package org.hrithik.documenteditor.schemas;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "document_elements")
public class DocumentElementSchema {
    @Id
    private String id;
    private String documentId;
    private String type;
    private String content;
}
