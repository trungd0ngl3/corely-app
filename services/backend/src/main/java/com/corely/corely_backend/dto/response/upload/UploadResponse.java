package com.corely.corely_backend.dto.response.upload;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadResponse {
    String publicId;
    String url;
    Integer width;
    Integer height;
    Long bytes;
    String format;
}
