package org.hrithik.documenteditor.persistence;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DatabasePersistence implements PersistenceInterface {

    @Override
    public void save(String text) {
        System.out.println("Saving to database: "+text);
    }
}
