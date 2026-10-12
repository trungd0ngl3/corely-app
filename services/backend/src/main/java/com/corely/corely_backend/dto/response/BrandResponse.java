package com.corely.corely_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandResponse {
    UUID id;
    String name;
    String slug;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
