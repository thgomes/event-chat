package com.eventchat.application.port.output;

import com.eventchat.domain.model.ChatMessage;

import java.util.List;

public interface ChatMessageRepository {
    void save(ChatMessage message);
    List<ChatMessage> findAllOrderByTime();
}
