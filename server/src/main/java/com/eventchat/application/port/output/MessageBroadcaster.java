package com.eventchat.application.port.output;

import com.eventchat.application.dto.MessageDto;

public interface MessageBroadcaster {
    void broadcast(MessageDto message);
    void sendToSession(String sessionId, MessageDto message);
}
