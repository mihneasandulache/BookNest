package com.booknest.dto.userprofile;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String bio;
    private String avatarUrl;
    private String location;
}
