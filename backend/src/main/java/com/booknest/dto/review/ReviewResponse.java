package com.booknest.dto.review;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String username;
    private int rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
