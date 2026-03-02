package com.eventchat.adapters.rest;

import com.eventchat.application.dto.FileUploadDto;
import com.eventchat.application.port.input.UploadFilePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;

@Path("/uploads")
@ApplicationScoped
public class FileUploadResource {

    @Inject
    UploadFilePort uploadFilePort;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(@RestForm("file") FileUpload file) {
        if (file == null || file.fileName() == null || file.fileName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try (var content = Files.newInputStream(file.uploadedFile())) {
            FileUploadDto result = uploadFilePort.upload(file.fileName(), content);
            return Response.ok(result).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
