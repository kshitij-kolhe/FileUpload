package com.kshitij.FileUpload.controller;


import com.kshitij.FileUpload.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class FileDownloadController {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileDownloadController(final FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(value = "/file")
    public ResponseEntity<byte[]> downloadFile(@RequestParam final String file) throws IOException {

        if (fileStorageService.isFilePresent(file)) {
            return getFileResponseEntity(file);
        }

        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<byte[]> getFileResponseEntity(String file) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(file).build().toString());

        return ResponseEntity.ok().headers(httpHeaders).body(fileStorageService.readFile(file).readAllBytes());
    }
}
