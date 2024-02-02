package com.kshitij.FileUpload.controller;

import com.kshitij.FileUpload.service.FileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = FileDownloadController.class)
class FileDownloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 1})
    void downloadFile(final int size) throws Exception {
        final LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("file", "app.pdf");

        final RequestBuilder request = MockMvcRequestBuilders.get("/file")
                .queryParams(params);

        final byte[] bytes = new byte[size];
        IntStream.range(0, size).forEach(i -> bytes[i] = (byte) (i + 15));
        final ByteArrayInputStream expectedFileStream = new ByteArrayInputStream(bytes);

        when(fileStorageService.isFilePresent(anyString())).thenReturn(true);
        when(fileStorageService.readFile(anyString())).thenReturn(expectedFileStream);

        final MvcResult mvcResult = mockMvc.perform(request).andReturn();

        Assertions.assertEquals(200, mvcResult.getResponse().getStatus());

        expectedFileStream.reset();
        Assertions.assertArrayEquals(expectedFileStream.readAllBytes(), mvcResult.getResponse().getContentAsByteArray());
    }
}