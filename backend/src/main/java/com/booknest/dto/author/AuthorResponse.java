package com.booknest.dto.author;

import lombok.Data;

@Data
public class AuthorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String bio;
    private String nationality;
}
