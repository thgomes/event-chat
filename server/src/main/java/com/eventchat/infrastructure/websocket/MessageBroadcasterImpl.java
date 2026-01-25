package com.eventchat.infrastructure.websocket;

import com.eventchat.application.dto.MessageDto;
import com.eventchat.application.port.output.MessageBroadcaster;
import com.eventchat.application.port.output.SessionManager;
import com.eventchat.application.port.output.WebSocketSessionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MessageBroadcasterImpl implements MessageBroadcaster {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    WebSocketSessionRegistry sessionRegistry;

    @Inject
    SessionManager sessionManager;

    @Override
    public void broadcast(MessageDto message) {
        String json = serialize(message);
        List<String> closed = new ArrayList<>();

        for (Session session : new ArrayList<>(sessionRegistry.getAllSessions())) {
            if (session != null && session.isOpen()) {
                try {
                    session.getAsyncRemote().sendText(json);
                } catch (Exception e) {
                    closed.add(session.getId());
                }
            } else {
                closed.add(session.getId());
            }
        }

        closed.forEach(sessionId -> {
            sessionRegistry.unregister(sessionId);
            sessionManager.unregister(sessionId);
        });
    }

    @Override
    public void sendToSession(String sessionId, MessageDto message) {
        Session session = sessionRegistry.getSession(sessionId);
        if (session != null && session.isOpen()) {
            try {
                String json = serialize(message);
                session.getAsyncRemote().sendText(json);
            } catch (Exception e) {
                System.out.println("Error sending message to session: " + e.getMessage());
            }
        }
    }

    private String serialize(MessageDto message) {
        try {
            return MAPPER.writeValueAsString(message);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }
}
