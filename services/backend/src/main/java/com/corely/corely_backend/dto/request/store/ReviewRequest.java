package com.corely.corely_backend.dto.request.store;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    @NotNull
    @Min(1)
    @Max(5)
    Integer rating;

    @NotBlank
    @Size(max = 1000)
    String comment;
}
