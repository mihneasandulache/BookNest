package com.booknest.dto.feedback;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponse {
    private Long id;
    private Long userId;
    private String username;
    private String category;
    private String contactMethod;
    private boolean subscribeNewsletter;
    private String message;
    private LocalDateTime submittedAt;
}
