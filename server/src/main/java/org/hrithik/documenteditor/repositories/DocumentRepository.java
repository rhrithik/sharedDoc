package org.hrithik.documenteditor.repositories;

import org.hrithik.documenteditor.schemas.DocumentSchema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentRepository extends MongoRepository<DocumentSchema,String> {
    @Query("{ 'userAccess.?0': { $exists: true } }")
    List<DocumentSchema> findByUserAccessContainingKey(String username);
}
