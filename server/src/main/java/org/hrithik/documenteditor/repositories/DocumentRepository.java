package org.hrithik.documenteditor.repositories;

import org.hrithik.documenteditor.schemas.DocumentSchema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentRepository extends MongoRepository<DocumentSchema,String> {
}
