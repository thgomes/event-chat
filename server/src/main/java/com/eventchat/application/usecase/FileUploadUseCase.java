package com.eventchat.application.usecase;

import com.eventchat.application.dto.FileUploadDto;
import com.eventchat.application.port.input.UploadFilePort;
import com.eventchat.application.port.output.FileStorage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;

@ApplicationScoped
public class FileUploadUseCase implements UploadFilePort {

    @Inject
    FileStorage fileStorage;

    @Override
    public FileUploadDto upload(String filename, InputStream content) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Nome do arquivo é obrigatório");
        }
        if (content == null) {
            throw new IllegalArgumentException("Conteúdo do arquivo é obrigatório");
        }

        String storedName = fileStorage.store(filename, content);
        return new FileUploadDto(filename, storedName);
    }
}
