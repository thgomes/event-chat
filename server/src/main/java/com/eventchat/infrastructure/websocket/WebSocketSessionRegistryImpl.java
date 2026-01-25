package com.eventchat.infrastructure.websocket;

import com.eventchat.application.port.output.WebSocketSessionRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class WebSocketSessionRegistryImpl implements WebSocketSessionRegistry {

    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void register(String sessionId, Session session) {
        sessions.put(sessionId, session);
    }

    @Override
    public void unregister(String sessionId) {
        sessions.remove(sessionId);
    }

    @Override
    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public java.util.Collection<Session> getAllSessions() {
        return new java.util.ArrayList<>(sessions.values());
    }
}
