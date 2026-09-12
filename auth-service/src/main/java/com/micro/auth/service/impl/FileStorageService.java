package com.micro.auth.service.impl;

import com.generic.service.exception.GenericException;
import com.micro.auth.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@Log4j2
public class FileStorageService implements StorageService {
    private static final File FILE_ROOT_PATH = new File(System.getProperty("user.dir") + File.separator + "file-uploads");
    private static final long MAX_FILE_SIZE = 1024 * 1024L;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/webp"
    );

    static {
        FILE_ROOT_PATH.mkdirs();
    }

    private String getFilePathAsBusinessKey(String fileName) {
        return (UUID.randomUUID().toString() + UUID.randomUUID()).replace("-", "") + Objects.requireNonNull(fileName).substring(fileName.lastIndexOf("."));
    }

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "File size exceeds 1MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "Invalid file type: " + contentType);
        }
        String filePath = getFilePathAsBusinessKey(file.getOriginalFilename());
        try (OutputStream outputStream = new FileOutputStream(FILE_ROOT_PATH.getAbsolutePath() + File.separator + filePath)) {
            StreamUtils.copy(file.getInputStream(), outputStream);
            return filePath;
        } catch (Exception e) {
            log.error("Error during file uploading ", e);
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), "File uploading error");
        }
    }

    @Override
    public InputStream getFile(String filePath) {
        if (!this.isFilePathObjectExists(filePath)) {
            throw new GenericException(HttpStatus.NOT_FOUND.value(), "File object not found");
        }
        try {
            return Files.newInputStream(new File(FILE_ROOT_PATH.getAbsolutePath() + File.separator + filePath).toPath());
        } catch (Exception e) {
            log.error("Error during file fetch ", e);
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), "File fetching error");
        }
    }

    @Override
    public boolean isFilePathObjectExists(String filePath) {
        if (filePath == null || filePath.isBlank() || filePath.lastIndexOf(".") == -1) {
            return false;
        }
        final var randomPath = getFilePathAsBusinessKey("shiv.png");
        if (filePath.substring(0, filePath.lastIndexOf(".")).length() != randomPath.substring(0, randomPath.lastIndexOf(".")).length()) {
            return false;
        }
        return new File(FILE_ROOT_PATH.getAbsolutePath() + File.separator + filePath).exists();
    }
}
