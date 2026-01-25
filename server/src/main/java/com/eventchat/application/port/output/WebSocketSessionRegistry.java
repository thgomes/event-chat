package com.eventchat.application.port.output;

import jakarta.websocket.Session;

public interface WebSocketSessionRegistry {
    void register(String sessionId, Session session);
    void unregister(String sessionId);
    Session getSession(String sessionId);
    java.util.Collection<Session> getAllSessions();
}
