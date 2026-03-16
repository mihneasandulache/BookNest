package com.booknest.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull
    private Long bookId;

    @Min(1) @Max(5)
    private int rating;

    private String content;
}
