package org.hrithik.documenteditor.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hrithik.documenteditor.classes.DocumentEditor;
import org.hrithik.documenteditor.classes.DocumentElement;
import org.hrithik.documenteditor.repositories.DocumentRepository;
import org.hrithik.documenteditor.schemas.DocumentSchema;
import org.hrithik.documenteditor.schemas.MessageSchema;
import org.hrithik.documenteditor.services.DocumentCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private DocumentEditor documentEditor;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentCacheService documentCacheService;

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    private ObjectMapper objectMapper;

    private final HashMap<String, List<WebSocketSession>> idSessionMapping = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

        if (session.isOpen()) {
            getDocumentList(session);


        }

        System.out.println("Connected to WebSocket Session: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        cleanupSession(session);
        System.out.println("Disconnected from WebSocket Session: " + session.getId());
    }

    private void cleanupSession(WebSocketSession session) {
        sessions.remove(session);
        idSessionMapping.values().forEach(list -> list.remove(session));
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        MessageSchema receivedMessage = objectMapper.readValue(payload, MessageSchema.class);
        System.out.println(payload);

        if (receivedMessage.getAction() != null) {

            switch (receivedMessage.getAction()) {
                case "getDocumentList":
                    getDocumentList(session);
                    break;
                case "getDocument":
                    getDocument(session, receivedMessage.getDocumentId());
                    break;
                case "createDocument":
                    createDocument(session, receivedMessage.getDocumentId());
                    break;
                case "edit":
                    editDocument(session, receivedMessage);
                    break;
                case "deleteDocument":
                    deleteDocument(receivedMessage.getDocumentId());
                    break;

                default:
                    break;

            }
        }

    }

    private void createDocument(WebSocketSession session, String documentId) {
        DocumentSchema doc = documentCacheService.createDocument(documentId);
        MessageSchema res = new MessageSchema(documentId, doc.getContent());
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(res)));
            }
        } catch (IOException e) {
            System.err.println("Failed to send message on connection established for session " + session.getId() + ": " + e.getMessage());
            cleanupSession(session);
        }

    }

    private void deleteDocument(String documentId) {
        documentCacheService.deleteDocument(documentId);
    }

    private void getDocument(WebSocketSession session, String documentId) {
        DocumentSchema doc = documentCacheService.getDocument(documentId);
        if (doc != null) {
            MessageSchema res = new MessageSchema("returnDocument", documentId, doc.getContent());

            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(res)));
                }
            } catch (IOException e) {
                System.err.println("Failed to send message on connection established for session " + session.getId() + ": " + e.getMessage());
                cleanupSession(session);
            }
            idSessionMapping.putIfAbsent(doc.getId(), new CopyOnWriteArrayList<>());
            idSessionMapping.get(doc.getId()).add(session);
        }

    }

    private void getDocumentList(WebSocketSession session) {
        List<String> documentIdList = new ArrayList<>();
        List<DocumentSchema> documentList = documentRepository.findAll();
        for (DocumentSchema i : documentList) {
            documentIdList.add(i.getId());
        }
        try {


            MessageSchema message = new MessageSchema("documentList", documentIdList);

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            System.err.println("Cannot send message");
        }


    }

    private void editDocument(WebSocketSession session, MessageSchema receivedMessage) {
        String documentId = receivedMessage.getDocumentId();

        documentCacheService.saveDocument(documentId, receivedMessage.getMessage());


        List<WebSocketSession> sessionList = idSessionMapping.get(receivedMessage.getDocumentId());
        if (sessionList == null) {
            System.err.println("No sessions found for document ID: " + receivedMessage.getDocumentId());
            return;
        }

        List<WebSocketSession> toBeRemoved = new ArrayList<>();

        for (WebSocketSession curr : sessionList) {
            if (!curr.getId().equals(session.getId())) {
                if (curr.isOpen()) {
                    try {
                        curr.sendMessage(new TextMessage(objectMapper.writeValueAsString(receivedMessage)));
                    } catch (IOException e) {
                        System.err.println("Failed to send message to session " + curr.getId() + " : " + e.getMessage());
                        cleanupSession(curr);
                        toBeRemoved.add(curr);
                    }
                } else {
                    toBeRemoved.add(curr);
                }
            }
        }

        sessionList.removeAll(toBeRemoved);

    }


    private void broadcast(String message) {
        Iterator<WebSocketSession> iterator = sessions.iterator();
        while (iterator.hasNext()) {
            WebSocketSession session = iterator.next();
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("Error broadcasting to session " + session.getId() + ": " + e.getMessage());
                    cleanupSession(session);
                    iterator.remove();
                }
            } else {
                iterator.remove();
            }
        }
    }
}
