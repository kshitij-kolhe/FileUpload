package com.kshitij.FileUpload.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kshitij.FileUpload.service.FileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.kshitij.FileUpload.service.FileStorageService.FOLDER;


@SpringBootTest
@AutoConfigureMockMvc
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper deserializer;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() throws IOException {

        Files.deleteIfExists(Path.of(FOLDER + "/upload.txt"));
        Files.deleteIfExists(Path.of(FOLDER + "/file2.txt"));
        Files.deleteIfExists(Path.of(FOLDER + "/store.txt"));
        Files.deleteIfExists(Path.of(FOLDER));
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt"})
    public void testListFiles_whenFilesArePresent_thenReturnListOfAllFiles(final String file) throws Exception {
        //creating a GET request to get all files name, from endpoint "/files"
        final RequestBuilder request = MockMvcRequestBuilders.get("/files");

        //Creating file, to fetch the actual file names stored in server
        final List<String> expectedFiles = List.of("file2.txt", file);
        fileStorageService.createFolder();
        fileStorageService.createNew(file,  new ByteArrayInputStream("File text for endpoint to get all file names".getBytes()));
        fileStorageService.createNew("file2.txt",  new ByteArrayInputStream("File text for endpoint to get all file names".getBytes()));

        //sending request
        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        //fetching result
        final List<String> actualFiles = deserializer.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        //Asserting
        Assertions.assertIterableEquals(expectedFiles, actualFiles);
        Assertions.assertEquals(expectedFiles.size(), actualFiles.size());
    }

    @Test
    public void testListFiles_whenDirectoriesIsEmpty_thenReturnEmptyList() throws Exception {
        //creating a GET request to get all files name, from endpoint "/files"
        final RequestBuilder request = MockMvcRequestBuilders.get("/files");

        //Creating empty folder
        final List<String> expectedFiles = List.of();
        fileStorageService.createFolder();

        //sending request
        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        //fetching result
        final List<String> actualFiles = deserializer.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        //Asserting
        Assertions.assertIterableEquals(expectedFiles, actualFiles);
        Assertions.assertEquals(0, actualFiles.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt"})
    public void testFileUpload_whenMultipartFileUploadedAndFileNotPresent_thenStoreNewFileInServer(final String file) throws Exception {
        //Creating a file to upload
        final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file, "text/plain", "File text generated for testing upload controller".getBytes());

        //Building a POST Multipart request to upload the file
        final RequestBuilder request = MockMvcRequestBuilders.multipart("/file")
                .file(mockMultipartFile);

        //sending request
        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        //Asserting
        Assertions.assertEquals(201, mvcResult.getResponse().getStatus());
        Assertions.assertTrue(fileStorageService.isFilePresent(file));
        Assertions.assertArrayEquals("File text generated for testing upload controller".getBytes(), fileStorageService.readFile(file).readAllBytes());
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt"})
    public void testFileUpload_whenMultipartFileUploadedAndFileIsPresent_thenReplaceFileInServer(final String file) throws Exception {
        //Creating a file to replace an existing file on server
        final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file, "text/plain", "File text generated for testing upload controller endpoint to replace existing file".getBytes());

        //Creating a file on server to be replaced
        fileStorageService.createFolder();
        fileStorageService.createNew(file, new ByteArrayInputStream("File text generated for testing upload controller endpoint to be replaced".getBytes()));

        //Building a POST Multipart request to upload a file
        final RequestBuilder request = MockMvcRequestBuilders.multipart("/file")
                .file(mockMultipartFile);

        //sending request
        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        //Asserting
        Assertions.assertEquals(201, mvcResult.getResponse().getStatus());
        Assertions.assertArrayEquals("File text generated for testing upload controller endpoint to replace existing file".getBytes(), fileStorageService.readFile(file).readAllBytes());
    }
}