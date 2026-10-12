package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.response.upload.UploadResponse;
import com.corely.corely_backend.enums.UploadFolder;
import com.corely.corely_backend.validator.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final FileValidator fileValidator;
    private final CloudinaryService cloudinaryService;

    public UploadResponse uploadImage(MultipartFile file, UploadFolder folder) {
        fileValidator.validateImage(file);
        return cloudinaryService.upload(file, folder);
    }

    public void deleteImage(String publicId) {
        cloudinaryService.delete(publicId);
    }
}