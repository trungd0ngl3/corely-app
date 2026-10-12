package com.corely.corely_backend.dto.request.store;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
public class StoreUpdateRequest {

    @NotBlank
    private String name;

    private String description;

    private String logoUrl;

    private String bannerUrl;

    private String contactPhone;

    @Email
    private String contactEmail;

    private String address;
}
