package com.eventchat.domain.model;

public class ChatMessage {

    private Long id;
    private String author;
    private String content;
    private long msgTimestamp;

    public ChatMessage() {
    }

    public ChatMessage(String author, String content, long msgTimestamp) {
        this.author = author;
        this.content = content;
        this.msgTimestamp = msgTimestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(long msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }
}
