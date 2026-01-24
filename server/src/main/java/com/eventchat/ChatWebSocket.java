package com.eventchat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ServerEndpoint("/chat")
public class ChatWebSocket {

    private static final int MAX_USERS = 10;
    private static final ConcurrentHashMap<Session, String> SESSIONS = new ConcurrentHashMap<>();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ExecutorService WORKER = Executors.newFixedThreadPool(4);

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String raw, Session session) {
        try {
            JsonNode json = MAPPER.readTree(raw);
            String type = json.has("type") ? json.get("type").asText() : "";

            if (!SESSIONS.containsKey(session)) {
                if ("join".equals(type)) {
                    handleJoin(json, session);
                } else {
                    sendError(session, "Envie 'join' com seu nome primeiro.");
                    session.close();
                }
                return;
            }

            if ("message".equals(type)) {
                handleMessage(json, session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Mensagem inválida.");
        }
    }

    private void handleJoin(JsonNode json, Session session) throws IOException {
        String author = json.has("author") ? json.get("author").asText().trim() : "";
        if (author.isEmpty()) {
            sendError(session, "Nome não pode ser vazio.");
            session.close();
            return;
        }

        if (SESSIONS.size() >= MAX_USERS) {
            sendError(session, "Sala cheia. Máximo " + MAX_USERS + " usuários.");
            session.close();
            return;
        }

        if (SESSIONS.values().stream().anyMatch(name -> name.equalsIgnoreCase(author))) {
            sendError(session, "Nome já está em uso.");
            session.close();
            return;
        }

        SESSIONS.put(session, author);

        broadcastSystem(author + " entrou no chat.");
        broadcastUserList();
        WORKER.submit(() -> {
            sendHistory(session);
            sendJoinedOk(session, author);
        });
    }

    private void sendHistory(Session session) {
        try {
            ChatHistoryService history = ChatHistoryHolder.get();
            if (history == null) return;
            List<ChatMessage> list = history.findAllOrderByTime();
            ObjectNode payload = MAPPER.createObjectNode();
            payload.put("type", "history");
            ArrayNode arr = payload.putArray("messages");
            for (ChatMessage m : list) {
                ObjectNode o = MAPPER.createObjectNode();
                o.put("author", m.author);
                o.put("content", m.content);
                o.put("timestamp", m.msgTimestamp);
                arr.add(o);
            }
            send(session, MAPPER.writeValueAsString(payload));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendJoinedOk(Session session, String author) {
        try {
            ObjectNode joinedOk = MAPPER.createObjectNode();
            joinedOk.put("type", "joined_ok");
            joinedOk.put("author", author);
            ArrayNode users = joinedOk.putArray("users");
            SESSIONS.values().forEach(users::add);
            send(session, MAPPER.writeValueAsString(joinedOk));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(JsonNode json, Session session) {
        String author = SESSIONS.get(session);
        String content = json.has("content") ? json.get("content").asText() : "";
        long timestamp = json.has("timestamp") ? json.get("timestamp").asLong() : System.currentTimeMillis();

        WORKER.submit(() -> {
            try {
                ChatHistoryService history = ChatHistoryHolder.get();
                if (history != null) history.save(author, content, timestamp);
                ObjectNode msg = MAPPER.createObjectNode();
                msg.put("type", "message");
                msg.put("author", author);
                msg.put("content", content);
                msg.put("timestamp", timestamp);
                broadcast(MAPPER.writeValueAsString(msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @OnClose
    public void onClose(Session session) {
        String author = SESSIONS.remove(session);
        if (author != null) {
            broadcastSystem(author + " saiu do chat.");
            broadcastUserList();
        }
        System.out.println("Connection closed: " + session.getId());
    }

    private void broadcastSystem(String content) {
        try {
            ObjectNode sys = MAPPER.createObjectNode();
            sys.put("type", "system");
            sys.put("content", content);
            broadcast(MAPPER.writeValueAsString(sys));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastUserList() {
        try {
            ObjectNode payload = MAPPER.createObjectNode();
            payload.put("type", "user_list");
            ArrayNode users = payload.putArray("users");
            SESSIONS.values().forEach(users::add);
            broadcast(MAPPER.writeValueAsString(payload));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        List<Session> closed = new ArrayList<>();
        for (Session s : new ArrayList<>(SESSIONS.keySet())) {
            if (s.isOpen()) {
                try {
                    s.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    closed.add(s);
                }
            } else {
                closed.add(s);
            }
        }
        closed.forEach(SESSIONS::remove);
    }

    private void send(Session session, String message) {
        try {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendError(Session session, String content) {
        try {
            ObjectNode err = MAPPER.createObjectNode();
            err.put("type", "error");
            err.put("content", content);
            send(session, MAPPER.writeValueAsString(err));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
