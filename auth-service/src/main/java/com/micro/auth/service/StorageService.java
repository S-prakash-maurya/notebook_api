package com.micro.auth.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    String upload(MultipartFile multipartFile);

    InputStream getFile(String filePath);

    boolean isFilePathObjectExists(String filePath);
}
