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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@WebMvcTest(controllers = FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;
    private ObjectMapper deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt"})
    public void testListFiles_whenFilesArePresent_thenReturnListOfAllFiles(final String file) throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/files");
        final List<String> expectedFiles = List.of(file, "file2.txt");

        Mockito.when(fileStorageService.getAllFileNames()).thenReturn(expectedFiles);

        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        final List<String> actualFiles = deserializer.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        Assertions.assertIterableEquals(expectedFiles, actualFiles);
        Assertions.assertEquals(expectedFiles.size(), actualFiles.size());
    }

    @Test
    public void testListFiles_whenDirectoriesIsEmpty_thenReturnEmptyList() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/files");
        final List<String> expectedFiles = List.of();

        Mockito.when(fileStorageService.getAllFileNames()).thenReturn(expectedFiles);

        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        final List<String> actualFiles = deserializer.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        Assertions.assertIterableEquals(expectedFiles, actualFiles);
        Assertions.assertEquals(0, actualFiles.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt"})
    public void testFileUpload_whenMultipartFileUploadedAndFileNotPresent_thenStoreNewFileInServer(final String file) throws Exception {
        final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file, "text/plain", "File text generated for testing upload controller".getBytes());

        Mockito.doNothing().when(fileStorageService).creatFolder();
        Mockito.when(fileStorageService.isFilePresent(file)).thenReturn(false);
        Mockito.doNothing().when(fileStorageService).createNew(Mockito.anyString(), Mockito.any(InputStream.class));

        final RequestBuilder request = MockMvcRequestBuilders.multipart("/file")
                .file(mockMultipartFile);

        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        Assertions.assertEquals(201, mvcResult.getResponse().getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt"})
    public void testFileUpload_whenMultipartFileUploadedAndFileIsPresent_thenReplaceFileInServer(final String file) throws Exception {
        final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file, "text/plain", "File text generated for testing upload controller".getBytes());

        Mockito.doNothing().when(fileStorageService).creatFolder();
        Mockito.when(fileStorageService.isFilePresent(file)).thenReturn(true);
        Mockito.doNothing().when(fileStorageService).replaceExisting(Mockito.anyString(), Mockito.any(InputStream.class));

        final RequestBuilder request = MockMvcRequestBuilders.multipart("/file")
                .file(mockMultipartFile);

        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        Assertions.assertEquals(201, mvcResult.getResponse().getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {"upload.txt", "store.txt"})
    public void testFileUpload_whenExceptionThrown_thenFaileRequest(final String file) throws Exception {
        final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file, "text/plain", "File text generated for testing upload controller".getBytes());

        Mockito.doThrow(IOException.class).when(fileStorageService).creatFolder();

        final RequestBuilder request = MockMvcRequestBuilders.multipart("/file")
                .file(mockMultipartFile);

        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        Assertions.assertEquals(500, mvcResult.getResponse().getStatus());
    }
}