package org.hrithik.documenteditor.services;

import org.hrithik.documenteditor.repositories.DocumentRepository;
import org.hrithik.documenteditor.schemas.DocumentSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import javax.print.Doc;

@Service
public class DocumentCacheService {
    @Autowired
    private RedisTemplate<String, DocumentSchema> redisTemplate;

    @Autowired
    private DocumentRepository documentRepository;

    private static final String DOC_KEY_PREFIX = "doc:";
    private static final int CACHE_TTL_SECONDS = 900;

    public DocumentSchema getDocument(String documentId) {
        try {
            DocumentSchema cachedDoc = (DocumentSchema) redisTemplate.opsForValue().get(DOC_KEY_PREFIX + documentId);
            if(cachedDoc==null){
                DocumentSchema doc =  documentRepository.findById(documentId).orElse(null);
                if(doc==null){
                    return null;
                }
                System.out.println("Doc found");
                return doc;
            }
            System.out.println("There is a cached Doc");
            return cachedDoc;
        } catch (SerializationException ex) {
            System.err.println("SerializationException reading Redis for key " + documentId + ": " + ex.getMessage());
            redisTemplate.delete(DOC_KEY_PREFIX + documentId);
            return documentRepository.findById(documentId).orElse(null);
        }
    }

    public DocumentSchema createDocument(String documentId){
        String cacheKey = DOC_KEY_PREFIX+documentId;

        DocumentSchema doc = new DocumentSchema(documentId,"");
        documentRepository.save(doc);
        redisTemplate.opsForValue().set(cacheKey,doc,CACHE_TTL_SECONDS);
        return doc;
    }


    public void saveDocument(String documentId, String content){
        String cacheKey = DOC_KEY_PREFIX+documentId;

        DocumentSchema doc = documentRepository.findById(documentId).orElse(null);

        if(doc!=null){
            doc.setContent(content);
            documentRepository.save(doc);

            redisTemplate.opsForValue().set(cacheKey,doc,CACHE_TTL_SECONDS);
        }
    }

    public void deleteDocument(String documentId){
        String cacheKey = DOC_KEY_PREFIX+documentId;
        redisTemplate.delete(cacheKey);
        documentRepository.deleteById(documentId);
    }


}
