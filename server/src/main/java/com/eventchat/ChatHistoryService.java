package com.eventchat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@ActivateRequestContext
public class ChatHistoryService {

    @Transactional
    public void save(String author, String content, long timestamp) {
        ChatMessage m = new ChatMessage();
        m.author = author;
        m.content = content;
        m.msgTimestamp = timestamp;
        m.persist();
    }

    @Transactional
    public List<ChatMessage> findAllOrderByTime() {
        return ChatMessage.listAllOrderByTime();
    }
}
