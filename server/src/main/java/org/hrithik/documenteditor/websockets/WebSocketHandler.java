package org.hrithik.documenteditor.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hrithik.documenteditor.classes.DocumentEditor;
import org.hrithik.documenteditor.classes.DocumentElement;
import org.hrithik.documenteditor.repositories.DocumentRepository;
import org.hrithik.documenteditor.schemas.DocumentSchema;
import org.hrithik.documenteditor.schemas.MessageSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private DocumentEditor documentEditor;

    @Autowired
    private DocumentRepository documentRepository;

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    private ObjectMapper objectMapper;

    private final HashMap<String, List<WebSocketSession>> idSessionMapping = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

        if (session.isOpen()) {
            DocumentSchema receivedDocument = documentRepository.findById("105").orElse(null);
            if (receivedDocument == null) {
                System.err.println("Document with ID 105 not found!");
                return;
            }

            idSessionMapping.putIfAbsent("105", new CopyOnWriteArrayList<>());
            idSessionMapping.get("105").add(session);

            MessageSchema receivedMessage = new MessageSchema(receivedDocument.getId(), receivedDocument.getContent());

            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(receivedMessage)));
                }
            } catch (IOException e) {
                System.err.println("Failed to send message on connection established for session " + session.getId() + ": " + e.getMessage());
                cleanupSession(session);
            }
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

        DocumentSchema doc = documentRepository.findById(receivedMessage.getDocumentId()).orElse(null);
        if (doc == null) {
            System.err.println("Document not found for ID: " + receivedMessage.getDocumentId());
            return;
        }

        doc.setContent(receivedMessage.getMessage());

        documentRepository.save(doc);

        List<WebSocketSession> sessionList = idSessionMapping.get(receivedMessage.getDocumentId());
        if (sessionList == null) {
            System.err.println("No sessions found for document ID: " + receivedMessage.getDocumentId());
            return;
        }

        Iterator<WebSocketSession> iterator = sessionList.iterator();
        while (iterator.hasNext()) {
            WebSocketSession webSocketSession = iterator.next();
            if (!webSocketSession.getId().equals(session.getId())) {
                if (webSocketSession.isOpen()) {
                    try {
                        webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(receivedMessage)));
                    } catch (IOException e) {
                        System.err.println("Failed to send message to session " + webSocketSession.getId() + ": " + e.getMessage());
                        cleanupSession(webSocketSession);
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
        }

        System.out.println("Received message: " + payload);
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
