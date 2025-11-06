package org.hrithik.documenteditor.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hrithik.documenteditor.classes.DocumentEditor;
import org.hrithik.documenteditor.classes.DocumentElement;
import org.hrithik.documenteditor.enums.accessEnum;
import org.hrithik.documenteditor.repositories.DocumentRepository;
import org.hrithik.documenteditor.schemas.DocumentSchema;
import org.hrithik.documenteditor.schemas.MessageSchema;
import org.hrithik.documenteditor.security.JwtTokenProvider;
import org.hrithik.documenteditor.services.DocumentCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private DocumentEditor documentEditor;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


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
        String token = session.getUri().getQuery().split("token=")[1];
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            session.getAttributes().put("username", username);
            System.out.println("User connected: " + username);
        } else {
            session.close();
            return;
        }
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
                    getDocument(session, receivedMessage.getDocumentId(), receivedMessage.getUsername());
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
                case "getSharedDocumentList":
                    getSharedDocumentList(session, receivedMessage.getDocumentId());
                    break;
                case "updateAccess":
                    updateDocumentAccess(session, receivedMessage.getDocumentId(), receivedMessage.getUsername(), receivedMessage.getAccess());
                    break;
                default:
                    break;

            }
        }

    }

    private void createDocument(WebSocketSession session, String documentId) {
        String username = (String) session.getAttributes().get("username");

        DocumentSchema doc = new DocumentSchema(documentId, "", username);
//
//        documentRepository.save(doc);
        documentCacheService.createDocument(documentId, username);

        MessageSchema res = MessageSchema.simpleMessage("documentCreated", documentId);
        try {
            if (session.isOpen()) {
                safeSend(session,new TextMessage(objectMapper.writeValueAsString(res)));
//                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(res)));
            }
        } catch (IOException e) {
            cleanupSession(session);
        }
    }

    private void deleteDocument(String documentId) {
        documentCacheService.deleteDocument(documentId);
    }

    private void getDocument(WebSocketSession session, String documentId, String username) {
        DocumentSchema doc = documentCacheService.getDocument(documentId);

        if (doc != null) {
            String access = doc.getUserAccess().getOrDefault(username, "");
            MessageSchema res = MessageSchema.getSingleDocument("returnDocument", documentId, doc.getContent(), access);

            try {
                if (session.isOpen()) {
                    safeSend(session,new TextMessage(objectMapper.writeValueAsString(res)));
//                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(res)));
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
//        String token = session.getHandshakeHeaders().getFirst("Authorization");
//        if(token!=null && token.startsWith("Bearer")){
//            token = token.substring(7);
//        }
//
//
//        List<String> documentIdList = new ArrayList<>();
//        List<DocumentSchema> documentList = documentRepository.findAll();
//        for (DocumentSchema i : documentList) {
//            documentIdList.add(i.getId());
//        }
        try {
            Object usernameAttr = session.getAttributes().get("username");
            if (usernameAttr == null) {
                System.err.println("No username found in session.");
                return;
            }
            String username = usernameAttr.toString();

            List<String[]> documentIdList = new ArrayList<>();
            List<DocumentSchema> allDocs = documentRepository.findByUserAccessContainingKey(username);

            for (DocumentSchema doc : allDocs) {
                if (doc.getUserAccess() != null && doc.getUserAccess().containsKey(username)) {
                    documentIdList.add(new String[]{doc.getId(), doc.getUserAccess().get(username)});
                }
            }

//            MessageSchema message = new MessageSchema("documentList", documentIdList);
            MessageSchema message = MessageSchema.getDocumentIdList("documentList", documentIdList);

            safeSend(session,new TextMessage(objectMapper.writeValueAsString(message)));
//            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            System.err.println("Cannot send message");
        }


    }

    private void editDocument(WebSocketSession session, MessageSchema receivedMessage) {
        String documentId = receivedMessage.getDocumentId();
        String author = (String)session.getAttributes().get("username");
        DocumentSchema doc = documentCacheService.getDocument(documentId);

        if(!doc.getUserAccess().containsKey(author) || doc.getUserAccess().get(author).equals("READ"))return;

        documentCacheService.saveDocument(documentId, receivedMessage.getMessage());


        List<WebSocketSession> sessionList = idSessionMapping.get(receivedMessage.getDocumentId());
        if (sessionList == null) {
            System.err.println("No sessions found for document ID: " + receivedMessage.getDocumentId());
            return;
        }

        List<WebSocketSession> toBeRemoved = new ArrayList<>();


        for (WebSocketSession curr : sessionList) {
            String username = (String) curr.getAttributes().get("username");
            if(!doc.getUserAccess().containsKey(username))continue;
            if (!curr.getId().equals(session.getId())) {
                if (curr.isOpen()) {
                    try {
                        safeSend(curr, new TextMessage(objectMapper.writeValueAsString(receivedMessage)));
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

    private void getSharedDocumentList(WebSocketSession session, String documentId) {
        try {

            DocumentSchema docs = documentCacheService.getDocument(documentId);
            List<String[]> sharedDocumentList = new ArrayList<>();
            for (String i : docs.getUserAccess().keySet()) {
                sharedDocumentList.add(new String[]{i, docs.getUserAccess().get(i)});
            }
            for (String i[] : sharedDocumentList) {
                System.out.println(Arrays.toString(i));
            }

            MessageSchema message = MessageSchema.getDocumentIdList("sharedDocumentList", sharedDocumentList);

            safeSend(session,new TextMessage(objectMapper.writeValueAsString(message)));
//            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            System.err.println("Failed to send message on connection established for session " + session.getId() + ": " + e.getMessage());
            cleanupSession(session);
        }


    }

    private void updateDocumentAccess(WebSocketSession session, String documentId, String username, String access) {
        try {

            DocumentSchema doc = documentCacheService.getDocument(documentId);
            HashMap<String, String> userAccess = doc.getUserAccess();
            if (access.equals("delete") ){
                userAccess.remove(username);
            } else {
                userAccess.put(username, access);
            }
            doc.setUserAccess(userAccess);
            documentCacheService.saveDocument(doc);
        } catch (Exception e) {
            System.err.println("Failed to send message on connection established for session " + session.getId() + ": " + e.getMessage());
            cleanupSession(session);
        }

    }

    private void safeSend(WebSocketSession session, TextMessage msg) {
        synchronized (session) {
            try {
                session.sendMessage(msg);
            } catch (IOException e) {
                System.err.println("Failed to send message: " + e.getMessage());
            }
        }
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
