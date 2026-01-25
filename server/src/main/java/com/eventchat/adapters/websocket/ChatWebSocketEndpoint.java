package com.eventchat.adapters.websocket;

import com.eventchat.application.dto.MessageDto;
import com.eventchat.application.port.output.MessageBroadcaster;
import com.eventchat.application.port.output.SessionManager;
import com.eventchat.application.port.output.TaskExecutor;
import com.eventchat.application.port.output.WebSocketSessionRegistry;
import com.eventchat.application.port.input.ChatInputPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
@ApplicationScoped
public class ChatWebSocketEndpoint {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    ChatInputPort chatInputPort;

    @Inject
    MessageBroadcaster broadcaster;

    @Inject
    TaskExecutor executor;

    @Inject
    WebSocketSessionRegistry registry;

    @Inject
    SessionManager sessionManager;

    @OnOpen
    public void onOpen(Session session) {
        registry.register(session.getId(), session);
    }

    @OnMessage
    public void onMessage(String raw, Session session) {
        try {
            JsonNode json = MAPPER.readTree(raw);
            String type = json.has("type") ? json.get("type").asText() : "";

            if (!isSessionRegistered(session.getId())) {
                if ("join".equals(type)) {
                    String author = extractAuthor(json);
                    executor.execute(() -> {
                        try {
                            chatInputPort.join(session.getId(), author);
                        } catch (ChatInputPort.JoinException e) {
                            sendError(session, broadcaster, e.getMessage());
                            try {
                                session.close();
                            } catch (Exception ex) {
                                System.out.println("Error closing session: " + ex.getMessage());
                            }
                        }
                    });
                } else {
                    sendError(session, broadcaster, "Envie 'join' com seu nome primeiro.");
                    try {
                        session.close();
                    } catch (Exception ex) {
                        System.out.println("Error closing session: " + ex.getMessage());
                    }
                }
            } else if ("message".equals(type)) {
                String content = extractContent(json);
                long timestamp = extractTimestamp(json);
                executor.execute(() -> {
                    chatInputPort.send(session.getId(), content, timestamp);
                });
            }
        } catch (Exception e) {
            sendError(session, broadcaster, "Mensagem inválida.");
        }
    }

    @OnClose
    public void onClose(Session session) {
        registry.unregister(session.getId());
        chatInputPort.disconnect(session.getId());
    }

    private boolean isSessionRegistered(String sessionId) {
        return sessionManager.getUsername(sessionId) != null;
    }

    private void sendError(Session session, MessageBroadcaster broadcaster, String content) {
        if (session != null && session.isOpen() && broadcaster != null) {
            broadcaster.sendToSession(session.getId(), MessageDto.error(content));
        }
    }

    private String extractAuthor(JsonNode json) {
        return json.has("author") ? json.get("author").asText().trim() : "";
    }

    private String extractContent(JsonNode json) {
        return json.has("content") ? json.get("content").asText() : "";
    }

    private long extractTimestamp(JsonNode json) {
        return json.has("timestamp") ? json.get("timestamp").asLong() : System.currentTimeMillis();
    }
}
