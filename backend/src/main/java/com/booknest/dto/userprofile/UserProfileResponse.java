package com.booknest.dto.userprofile;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String bio;
    private String avatarUrl;
    private String location;
    private LocalDateTime joinedAt;
}
