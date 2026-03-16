package com.booknest.service;

import com.booknest.dto.userprofile.UserProfileRequest;
import com.booknest.dto.userprofile.UserProfileResponse;
import com.booknest.entity.UserProfile;
import com.booknest.repository.UserProfileRepository;
import com.booknest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileResponse getByUserId(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        return toResponse(profile);
    }

    public UserProfileResponse update(Long userId, UserProfileRequest req) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        profile.setBio(req.getBio());
        profile.setAvatarUrl(req.getAvatarUrl());
        profile.setLocation(req.getLocation());
        return toResponse(userProfileRepository.save(profile));
    }

    private UserProfileResponse toResponse(UserProfile p) {
        UserProfileResponse res = new UserProfileResponse();
        res.setId(p.getId());
        res.setUserId(p.getUser().getId());
        res.setUsername(p.getUser().getUsername());
        res.setEmail(p.getUser().getEmail());
        res.setBio(p.getBio());
        res.setAvatarUrl(p.getAvatarUrl());
        res.setLocation(p.getLocation());
        res.setJoinedAt(p.getJoinedAt());
        return res;
    }
}
