package org.hrithik.documenteditor.controllers;

import org.hrithik.documenteditor.repositories.DocumentRepository;
import org.hrithik.documenteditor.schemas.DocumentSchema;
import org.hrithik.documenteditor.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public DocumentController(DocumentRepository documentRepository, JwtTokenProvider jwtTokenProvider) {
        this.documentRepository = documentRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/my")
    public ResponseEntity<List<DocumentSchema>> getMyDocuments(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        String username = jwtTokenProvider.getUsernameFromToken(token);
        List<DocumentSchema> docs = documentRepository.findByUserAccessContainingKey(username);
        return ResponseEntity.ok(docs);
    }
}
