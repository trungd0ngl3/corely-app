package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.response.upload.UploadResponse;
import com.corely.corely_backend.enums.UploadFolder;
import com.corely.corely_backend.service.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UploadControllerTest {

    private UploadService uploadService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        uploadService = mock(UploadService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UploadController(uploadService)).build();
    }

    @Test
    void uploadImageUsesGeneralFolderWhenFolderIsOmitted() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", new byte[]{1, 2, 3});
        when(uploadService.uploadImage(any(), eq(UploadFolder.GENERAL)))
                .thenReturn(UploadResponse.builder()
                        .publicId("corely/general/image")
                        .url("https://example.com/image.png")
                        .width(100)
                        .height(80)
                        .bytes(3L)
                        .format("png")
                        .build());

        mockMvc.perform(multipart("/upload/image").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.publicId").value("corely/general/image"))
                .andExpect(jsonPath("$.result.url").value("https://example.com/image.png"));

        verify(uploadService).uploadImage(any(), eq(UploadFolder.GENERAL));
    }

    @Test
    void uploadImagePassesRequestedFolderToService() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", new byte[]{1, 2, 3});
        when(uploadService.uploadImage(any(), eq(UploadFolder.PRODUCT)))
                .thenReturn(UploadResponse.builder().publicId("corely/products/image").build());

        mockMvc.perform(multipart("/upload/image")
                        .file(file)
                        .param("folder", "PRODUCT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.publicId").value("corely/products/image"));

        verify(uploadService).uploadImage(any(), eq(UploadFolder.PRODUCT));
    }

    @Test
    void deleteImagePassesPublicIdToService() throws Exception {
        mockMvc.perform(delete("/upload/image/{publicId}", "corely-products-image"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(uploadService).deleteImage("corely-products-image");
    }
}
