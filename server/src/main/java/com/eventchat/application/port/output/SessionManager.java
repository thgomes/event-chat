package com.eventchat.application.port.output;

import java.util.List;

public interface SessionManager {
    void register(String sessionId, String username);
    String unregister(String sessionId);
    String getUsername(String sessionId);
    List<String> getAllUsernames();
    int getUserCount();
    boolean isNameTaken(String name);
}
