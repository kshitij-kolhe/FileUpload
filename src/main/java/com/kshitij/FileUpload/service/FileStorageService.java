package com.kshitij.FileUpload.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileStorageService {

    String FOLDER = "./upload";

    boolean isFilePresent(final String file);

    void createFolder() throws IOException;

    void replaceExisting(final String file, final InputStream fileStream) throws IOException;

    void createNew(final String file, final InputStream bytes) throws IOException;

    InputStream readFile(final String file) throws IOException;

    List<String> getAllFileNames();
}
