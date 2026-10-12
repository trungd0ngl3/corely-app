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
public class AddressResponse {
    UUID id;
    String streetAddress;
    String ward;
    String district;
    String city;
    Boolean isDefault;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
