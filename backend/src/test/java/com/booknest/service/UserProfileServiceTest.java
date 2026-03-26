package com.booknest.service;

import com.booknest.dto.userprofile.UserProfileRequest;
import com.booknest.dto.userprofile.UserProfileResponse;
import com.booknest.entity.User;
import com.booknest.entity.UserProfile;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private User user;
    private UserProfile profile;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("encoded")
                .build();

        profile = UserProfile.builder()
                .id(5L)
                .user(user)
                .bio("Book lover")
                .avatarUrl("http://example.com/avatar.jpg")
                .location("New York")
                .joinedAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();
    }

    @Test
    void getByUserId_returnsProfile_whenExists() {
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        UserProfileResponse result = userProfileService.getByUserId(1L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        assertThat(result.getBio()).isEqualTo("Book lover");
        assertThat(result.getLocation()).isEqualTo("New York");
    }

    @Test
    void getByUserId_throwsResourceNotFoundException_whenNotFound() {
        when(userProfileRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getByUserId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_updatesAndReturnsProfile_whenExists() {
        UserProfileRequest req = new UserProfileRequest();
        req.setBio("Updated bio");
        req.setAvatarUrl("http://example.com/new-avatar.jpg");
        req.setLocation("Los Angeles");

        UserProfile updated = UserProfile.builder()
                .id(5L).user(user)
                .bio("Updated bio")
                .avatarUrl("http://example.com/new-avatar.jpg")
                .location("Los Angeles")
                .joinedAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updated);

        UserProfileResponse result = userProfileService.update(1L, req);

        assertThat(result.getBio()).isEqualTo("Updated bio");
        assertThat(result.getLocation()).isEqualTo("Los Angeles");
        assertThat(result.getAvatarUrl()).isEqualTo("http://example.com/new-avatar.jpg");
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(userProfileRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.update(99L, new UserProfileRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByUserId_mapsJoinedAt() {
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        UserProfileResponse result = userProfileService.getByUserId(1L);

        assertThat(result.getJoinedAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0));
    }
}
