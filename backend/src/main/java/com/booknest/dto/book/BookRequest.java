package com.booknest.dto.book;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class BookRequest {

    @NotBlank
    private String title;

    private String isbn;
    private Integer publishedYear;
    private String description;
    private String coverImageUrl;
    private Set<Long> authorIds;
    private Set<Long> genreIds;
}
