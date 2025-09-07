package org.hrithik.documenteditor.classes;

import org.hrithik.documenteditor.persistence.PersistenceInterface;
import org.hrithik.documenteditor.repositories.DocumentRepository;
import org.hrithik.documenteditor.schemas.DocumentSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentEditor {
    AppDocument appDocument;
    PersistenceInterface persistence;
    DocumentRepository documentRepository;

    @Autowired
    public DocumentEditor(PersistenceInterface persistence) {
        appDocument =new AppDocument();
        this.persistence = persistence;

    }

    public void addText(String text, String documentId){
        DocumentSchema ds = documentRepository.findById(documentId).get();
        DocumentElement textElement = new TextElement(text);
        appDocument.addDocumentElement(textElement);
    }

    public void addImage(String imagePath){
        DocumentElement imageElement = new ImageElement(imagePath);
        appDocument.addDocumentElement(imageElement);
    }


    public void renderDocument(){
        appDocument.render();
    }

    void save(){
        String serializedDocument = appDocument.serialize();
        persistence.save(serializedDocument);
    }
}
