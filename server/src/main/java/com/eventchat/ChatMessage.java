package com.eventchat;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_message")
public class ChatMessage extends PanacheEntity {

    public String author;
    public String content;
    public long msgTimestamp;

    public static java.util.List<ChatMessage> listAllOrderByTime() {
        return list("FROM ChatMessage ORDER BY msgTimestamp ASC");
    }
}
