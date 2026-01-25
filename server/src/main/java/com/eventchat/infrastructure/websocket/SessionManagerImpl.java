package com.eventchat.infrastructure.websocket;

import com.eventchat.application.port.output.SessionManager;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionManagerImpl implements SessionManager {

    private final ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();

    @Override
    public void register(String sessionId, String username) {
        sessions.put(sessionId, username);
    }

    @Override
    public String unregister(String sessionId) {
        return sessions.remove(sessionId);
    }

    @Override
    public String getUsername(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public List<String> getAllUsernames() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public int getUserCount() {
        return sessions.size();
    }

    @Override
    public boolean isNameTaken(String name) {
        return sessions.values().stream()
            .anyMatch(existing -> existing.equalsIgnoreCase(name));
    }
}
