package com.kshitij.FileUpload.controller;

import com.kshitij.FileUpload.service.FileStorageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileUploadController(final FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestParam("file")MultipartFile file, final HttpServletResponse httpServletResponse) {

        try (InputStream fileStream = file.getInputStream()) {
            storeFile(file, fileStream);
        }catch (IOException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
    }

    @GetMapping("/files")
    public List<String> listFiles(final HttpServletResponse httpServletResponse) {
        final List<String> files = fileStorageService.getAllFileNames();

        if (files.isEmpty()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            httpServletResponse.setStatus(httpServletResponse.SC_OK);
        }

        return files;
    }

    private void storeFile(final MultipartFile file, final InputStream fileStream) throws IOException {
        fileStorageService.createFolder();

        if (fileStorageService.isFilePresent(file.getOriginalFilename())) {
            fileStorageService.replaceExisting(file.getOriginalFilename(), fileStream);
        } else {
            fileStorageService.createNew(file.getOriginalFilename(), fileStream);
        }
    }
}
