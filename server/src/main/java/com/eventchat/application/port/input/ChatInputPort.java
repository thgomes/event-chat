package com.eventchat.application.port.input;

public interface ChatInputPort {
    void join(String sessionId, String author) throws JoinException;
    void send(String sessionId, String content, long timestamp);
    void disconnect(String sessionId);

    class JoinException extends Exception {
        public JoinException(String message) {
            super(message);
        }
    }
}
