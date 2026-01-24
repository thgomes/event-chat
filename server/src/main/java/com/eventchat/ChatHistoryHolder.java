package com.eventchat;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatHistoryHolder {

    @Inject
    ChatHistoryService service;

    private static volatile ChatHistoryService instance;

    void onStart(@Observes StartupEvent event) {
        instance = service;
    }

    public static ChatHistoryService get() {
        return instance;
    }
}
