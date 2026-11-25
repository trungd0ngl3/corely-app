package com.corely.corely_backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.corely.corely_backend.dto.response.upload.UploadResponse;
import com.corely.corely_backend.enums.UploadFolder;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    public UploadResponse upload(MultipartFile file, UploadFolder folder) {
        try {
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "corely/" + folder.getFolderName(),
                            "resource_type", "image",
                            "overwrite", true,
                            "unique_filename", true
                    )
            );

            Number width = (Number) result.get("width");
            Number height = (Number) result.get("height");
            Number bytes = (Number) result.get("bytes");

            log.info("Uploaded image publicId={}, folder={}, size={} bytes",
                    result.get("public_id"), folder.getFolderName(), file.getSize());

            return UploadResponse.builder()
                    .publicId((String) result.get("public_id"))
                    .url((String) result.get("secure_url"))
                    .format((String) result.get("format"))
                    .width(width != null ? width.intValue() : null)
                    .height(height != null ? height.intValue() : null)
                    .bytes(bytes != null ? bytes.longValue() : null)
                    .build();

        } catch (IOException e) {
            log.error("Upload failed: file={}, folder={}", file.getOriginalFilename(), folder.getFolderName(), e);
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    public void delete(String publicId) {
        try {
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String status = (String) result.get("result");

            if (!"ok".equals(status) && !"not found".equals(status)) {
                log.warn("Cloudinary delete unexpected status={}, publicId={}", status, publicId);
                throw new AppException(ErrorCode.IMAGE_DELETE_FAILED);
            }

            log.info("Deleted image publicId={}, status={}", publicId, status);
        } catch (IOException e) {
            log.error("Delete failed: publicId={}", publicId, e);
            throw new AppException(ErrorCode.IMAGE_DELETE_FAILED);
        }
    }
}