package org.hrithik.documenteditor.repositories;

import org.hrithik.documenteditor.schemas.UserSchema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserSchema, String> {
    Optional<UserSchema> findByUsername(String username);
}
