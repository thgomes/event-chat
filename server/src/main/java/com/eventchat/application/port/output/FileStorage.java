package com.eventchat.application.port.output;

import java.io.InputStream;
import java.nio.file.Path;

public interface FileStorage {

    String store(String filename, InputStream content);

    Path getUploadDirectory();
}
