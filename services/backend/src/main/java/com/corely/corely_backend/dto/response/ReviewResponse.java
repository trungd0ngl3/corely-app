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
public class ReviewResponse {
    UUID id;
    Integer rating;
    String comment;
    String userName;
    String avatarUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
