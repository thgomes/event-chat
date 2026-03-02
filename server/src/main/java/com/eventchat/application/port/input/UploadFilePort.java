package com.eventchat.application.port.input;

import com.eventchat.application.dto.FileUploadDto;

public interface UploadFilePort {

    FileUploadDto upload(String filename, java.io.InputStream content);
}
