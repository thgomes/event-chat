package com.eventchat.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageDto(
    String type,
    String author,
    String content,
    Long timestamp,
    String[] users,
    MessageDto[] messages
) {
    public static MessageDto joinedOk(String author, String[] users) {
        return new MessageDto("joined_ok", author, null, null, users, null);
    }

    public static MessageDto message(String author, String content, long timestamp) {
        return new MessageDto("message", author, content, timestamp, null, null);
    }

    public static MessageDto system(String content) {
        return new MessageDto("system", null, content, null, null, null);
    }

    public static MessageDto userList(String[] users) {
        return new MessageDto("user_list", null, null, null, users, null);
    }

    public static MessageDto history(MessageDto[] messages) {
        return new MessageDto("history", null, null, null, null, messages);
    }

    public static MessageDto error(String content) {
        return new MessageDto("error", null, content, null, null, null);
    }
}
