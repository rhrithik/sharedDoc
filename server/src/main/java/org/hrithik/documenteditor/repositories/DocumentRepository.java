package org.hrithik.documenteditor.repositories;

import org.hrithik.documenteditor.schemas.DocumentSchema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.Document;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentSchema,String> {
}
