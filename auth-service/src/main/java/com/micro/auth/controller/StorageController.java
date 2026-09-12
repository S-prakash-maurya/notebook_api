package com.micro.auth.controller;

import com.micro.auth.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/storage")
@AllArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @PostMapping(value = "/v1/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("filePath", storageService.upload(file)));
    }

    @GetMapping("/v1/fetch/{filePath}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("filePath") String filePath) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(storageService.getFile(filePath)));
    }
}
