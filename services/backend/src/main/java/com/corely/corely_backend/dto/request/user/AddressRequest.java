package com.corely.corely_backend.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
    @NotBlank
    String streetAddress;
    @NotBlank
    String ward;
    @NotBlank
    String district;
    @NotBlank
    String city;
    Boolean isDefault;
}
