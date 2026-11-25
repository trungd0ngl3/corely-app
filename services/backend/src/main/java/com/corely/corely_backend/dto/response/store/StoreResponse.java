package com.corely.corely_backend.dto.response.store;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StoreResponse {

    UUID id;

    String name;

    String slug;

    String description;

    String logoUrl;

    String bannerUrl;

    String contactPhone;

    String contactEmail;

    String address;

    Boolean isVerified;

    Boolean isActive;

    Double rating;

    LocalDateTime createdAt;
}
