package com.booknest.dto.author;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String bio;
    private String nationality;
}
