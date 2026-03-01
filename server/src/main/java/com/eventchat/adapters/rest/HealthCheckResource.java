package com.eventchat.adapters.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import com.eventchat.application.dto.MessageDto;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import jakarta.ws.rs.Produces;

@Path("/healthcheck")
@ApplicationScoped
public class HealthCheckResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<MessageDto> healthCheck() {
        return List.of(MessageDto.system("OK"));
    }
}