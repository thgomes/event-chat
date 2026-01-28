package com.eventchat.adapters.rest;

import com.eventchat.application.dto.MessageDto;
import com.eventchat.application.port.input.ChatInputPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/messages")
@ApplicationScoped
public class ChatHistoryResource {

    @Inject
    ChatInputPort chatInputPort;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<MessageDto> listMessages() {
        return chatInputPort.listHistory();
    }
}
