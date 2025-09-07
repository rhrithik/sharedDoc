package org.hrithik.documenteditor.persistence;


import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class InMemoryPersistence implements PersistenceInterface {
    private Map<String,String> cache;

    public void save(String text){
        cache.put("doc",text);

    }
}
