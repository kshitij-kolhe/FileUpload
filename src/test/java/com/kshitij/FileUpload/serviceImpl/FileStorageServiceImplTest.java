package com.kshitij.FileUpload.serviceImpl;

import com.kshitij.FileUpload.service.FileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.kshitij.FileUpload.service.FileStorageService.FOLDER;

class FileStorageServiceImplTest {

    private FileStorageService fileStorageService;

    @BeforeEach
    public void setUp() throws IOException {
        fileStorageService = new FileStorageServiceImpl();
        Files.createDirectories(Path.of("./upload"));
    }

    @AfterEach
    public void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of(FOLDER));
    }

    @Test
    void testCreatFolder_whenFolderCreated_returnTrue() throws IOException {
        fileStorageService.createFolder();
        Assertions.assertTrue(Files.isDirectory(Path.of(FOLDER)));

        Files.deleteIfExists(Path.of(FOLDER));
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt", "files.txt"})
    void testIsFilePresent_whenFilePresent_returnTrue(final String file) throws IOException {
        fileStorageService.createNew(file, new ByteArrayInputStream("File text for replacing file".getBytes()));

        Assertions.assertTrue(fileStorageService.isFilePresent(file));

        Files.deleteIfExists(Path.of(FOLDER + "/" + file));
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt", "files.txt"})
    void testIsFilePresent_whenFileNotPresent_returnFalse(final String file) throws IOException {
        Assertions.assertFalse(fileStorageService.isFilePresent(file));
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt", "files.txt"})
    void testReplaceExisting_whenFilePresent_thenReplaceFile(final String file) throws IOException {
        fileStorageService.createNew(file, new ByteArrayInputStream("File text for replacing file".getBytes()));

        fileStorageService.replaceExisting(file, new ByteArrayInputStream("File text for replacing file".getBytes()));

        final List<String> fileContent = Files.readAllLines(Path.of(FOLDER + "/" + file));

        Assertions.assertEquals("File text for replacing file", fileContent.get(0));
        Assertions.assertEquals(1, fileContent.size());

        Files.deleteIfExists(Path.of(FOLDER + "/" + file));
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt", "files.txt"})
    void testCreateNew_whenFileNotPresent_thenCreatNewFile(final String file) throws IOException {
        fileStorageService.createNew(file, new ByteArrayInputStream("File text for replacing file".getBytes()));

        Assertions.assertTrue(fileStorageService.isFilePresent(file));

        Files.deleteIfExists(Path.of(FOLDER + "/" + file));
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt", "files.txt"})
    void testGetAllFileNames_whenFilesPresent_returnListOfFileNames(final String file) throws IOException {
        for (int i = 1; i < 4; i++) {
            fileStorageService.createNew(i + file, new ByteArrayInputStream("File text for replacing file".getBytes()));
        }

        final List<String> files = fileStorageService.getAllFileNames();

        for (int i = 1; i < 4; i++) {
            Assertions.assertEquals(i + file, files.get(i - 1));
        }

        for (int i = 1; i < 4; i++) {
            Files.deleteIfExists(Path.of(FOLDER + "/" + i + file));
        }
    }

    @Test
    void testGetAllFileNames_whenFilesNotPresent_returnEmptyList() {
        final List<String> files = fileStorageService.getAllFileNames();

        Assertions.assertTrue(files.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"File content 1 for test", "File content 2 for test"})
    void testReadFile_whenFilePresent_returnFileInputStream(final String text) throws IOException {
        final InputStream expectedFileStream = new ByteArrayInputStream(text.getBytes());

        fileStorageService.createNew("testingFile.txt", expectedFileStream);

        final InputStream actualFileStream = fileStorageService.readFile("testingFile.txt");

        expectedFileStream.reset();
        Assertions.assertArrayEquals(expectedFileStream.readAllBytes(), actualFileStream.readAllBytes());

        Files.deleteIfExists(Path.of(FOLDER + "/testingFile.txt"));
    }
}