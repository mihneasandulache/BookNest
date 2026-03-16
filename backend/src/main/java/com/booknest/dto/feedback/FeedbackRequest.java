package com.booknest.dto.feedback;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackRequest {

    @NotBlank
    private String category;

    @NotBlank
    private String contactMethod;

    private boolean subscribeNewsletter;

    @NotBlank
    private String message;
}
