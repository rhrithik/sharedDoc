package org.hrithik.documenteditor.classes;

import java.util.ArrayList;
import java.util.List;

public class AppDocument {

    static final List<DocumentElement> documentElements = new ArrayList<>();

    void addDocumentElement(DocumentElement documentElement){
        documentElements.add(documentElement);
    }

    void render(){
        for(DocumentElement documentElement : documentElements){
            System.out.println(documentElement.toString());
        }
    }

    String serialize(){
        return documentElements.toString();
    }


}
