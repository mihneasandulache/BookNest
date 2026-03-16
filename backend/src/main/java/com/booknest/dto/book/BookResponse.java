package com.booknest.dto.book;

import com.booknest.dto.author.AuthorResponse;
import com.booknest.dto.genre.GenreResponse;
import lombok.Data;

import java.util.Set;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private String description;
    private String coverImageUrl;
    private Set<AuthorResponse> authors;
    private Set<GenreResponse> genres;
    private Double averageRating;
    private int reviewCount;
}
