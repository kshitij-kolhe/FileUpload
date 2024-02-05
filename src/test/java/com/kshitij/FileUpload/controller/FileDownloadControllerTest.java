package com.kshitij.FileUpload.controller;

import com.kshitij.FileUpload.service.FileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.kshitij.FileUpload.service.FileStorageService.FOLDER;

@SpringBootTest
@AutoConfigureMockMvc
class FileDownloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(FOLDER + "/app.pdf"));
        Files.deleteIfExists(Path.of(FOLDER));
    }

    @ParameterizedTest
    @ValueSource(strings = {"File random text 1", "file random text 2"})
    void testDownloadFile_whenFileIsPresent_thenDownloadTheFile(final String text) throws Exception {
        //file parameters for GET request to download file
        final LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("file", "app.pdf");

        //Creating a GET request to download the file using filename
        final RequestBuilder request = MockMvcRequestBuilders.get("/file")
                .queryParams(params);

        //Creating a file on the server to be downloaded
        fileStorageService.createFolder();
        fileStorageService.createNew("app.pdf", new ByteArrayInputStream(text.getBytes()));

        //sending request
        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        //Asserting
        Assertions.assertEquals(200, mvcResult.getResponse().getStatus());
        Assertions.assertArrayEquals(text.getBytes(), mvcResult.getResponse().getContentAsByteArray());
    }
}