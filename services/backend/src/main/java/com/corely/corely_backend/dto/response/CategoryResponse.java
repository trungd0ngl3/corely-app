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
public class CategoryResponse {
    UUID id;
    String name;
    String slug;
    String description;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
