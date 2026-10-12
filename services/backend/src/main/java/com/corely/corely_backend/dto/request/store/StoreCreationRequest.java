package com.corely.corely_backend.dto.request.store;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class StoreCreationRequest {
    @NotBlank(message = "STORE_NAME_REQUIRED")
    String name;

    String description;

    String logoUrl;

    String bannerUrl;

    String contactPhone;

    String contactEmail;

    String address;

    UUID ownerId;
}
