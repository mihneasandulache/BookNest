package com.booknest.dto.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreRequest {

    @NotBlank
    private String name;

    private String description;
}
