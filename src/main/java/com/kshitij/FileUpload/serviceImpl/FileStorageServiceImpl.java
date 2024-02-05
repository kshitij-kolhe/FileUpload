package com.kshitij.FileUpload.serviceImpl;

import com.kshitij.FileUpload.service.FileStorageService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

@Component
public class FileStorageServiceImpl implements FileStorageService {

    @Override
    public boolean isFilePresent(final String file) {
        return Files.exists(Path.of(FOLDER + "/" + file), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public void createFolder() throws IOException {
        Files.createDirectories(Path.of(FOLDER));
    }

    @Override
    public void replaceExisting(final String file, final InputStream fileStream) throws IOException {
        Files.copy(fileStream, Path.of(FOLDER + "/" + file), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void createNew(final String file, final InputStream fileStream) throws IOException {
        Files.write(Path.of(FOLDER + "/" + file), fileStream.readAllBytes());
    }

    @Override
    public InputStream readFile(final String file) throws IOException {
        return Files.newInputStream(Path.of(FOLDER + "/" + file));
    }

    @Override
    public List<String> getAllFileNames() {
        final File folder = new File(FOLDER + "/");

        if (folder.list() != null && folder.list().length > 0) {
            return List.of(folder.list());
        }

        return List.of();
    }
}
