package com.eventchat;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatWebSocket {

    @OnOpen
    public void onOpen(Session session) {
        send(session, "CONNECTED");
        System.out.println("Connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(message);
        send(session, message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Connection closed: " + session.getId());
    }

    private void send(Session session, String message) {
        try {
            session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
