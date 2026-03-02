package com.eventchat.infrastructure.storage;

import com.eventchat.application.port.output.FileStorage;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@ApplicationScoped
public class LocalFileStorage implements FileStorage {

    @ConfigProperty(name = "eventchat.upload.dir", defaultValue = "uploads")
    String uploadDir;

    @Override
    public String store(String filename, InputStream content) {
        String safeName = sanitizeFilename(filename);
        String storedName = UUID.randomUUID() + "_" + safeName;
        Path target = getUploadDirectory().resolve(storedName);

        try {
            Files.createDirectories(getUploadDirectory());
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
            return storedName;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo: " + e.getMessage());
        }
    }

    @Override
    public Path getUploadDirectory() {
        return Path.of(uploadDir);
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "file";
        }
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
