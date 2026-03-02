package com.eventchat.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FileUploadDto(
    String filename,
    String storedName
) {}
