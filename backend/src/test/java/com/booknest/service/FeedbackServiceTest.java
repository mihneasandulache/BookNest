package com.booknest.service;

import com.booknest.dto.feedback.FeedbackRequest;
import com.booknest.dto.feedback.FeedbackResponse;
import com.booknest.entity.Feedback;
import com.booknest.entity.User;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.repository.FeedbackRepository;
import com.booknest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private FeedbackService feedbackService;

    private User user;
    private Feedback feedback;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("encoded")
                .build();

        feedback = Feedback.builder()
                .id(10L)
                .user(user)
                .category("General")
                .contactMethod("email")
                .subscribeNewsletter(true)
                .message("Great platform!")
                .submittedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void submit_savesAndReturnsResponse() {
        FeedbackRequest req = new FeedbackRequest();
        req.setCategory("General");
        req.setContactMethod("email");
        req.setSubscribeNewsletter(true);
        req.setMessage("Great platform!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        FeedbackResponse result = feedbackService.submit(1L, req);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getCategory()).isEqualTo("General");
        assertThat(result.getMessage()).isEqualTo("Great platform!");
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.isSubscribeNewsletter()).isTrue();
        verify(feedbackRepository).save(any(Feedback.class));
        verify(emailService).sendFeedbackConfirmationEmail("alice@example.com", "alice");
    }

    @Test
    void submit_throwsResourceNotFoundException_whenUserNotFound() {
        FeedbackRequest req = new FeedbackRequest();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feedbackService.submit(99L, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getAll_returnsPageOfFeedback() {
        Pageable pageable = PageRequest.of(0, 10);
        when(feedbackRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(feedback)));

        Page<FeedbackResponse> result = feedbackService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("General");
    }

    @Test
    void delete_deletesFeedback_whenExists() {
        when(feedbackRepository.findById(10L)).thenReturn(Optional.of(feedback));

        feedbackService.delete(10L);

        verify(feedbackRepository).delete(feedback);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(feedbackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feedbackService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Feedback not found");
    }

    @Test
    void submit_withNullUser_toResponseHandlesNullUser() {
        // Verify toResponse handles feedback with null user gracefully
        Feedback noUserFeedback = Feedback.builder()
                .id(20L)
                .user(null)
                .category("Bug")
                .contactMethod("phone")
                .subscribeNewsletter(false)
                .message("Found a bug")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(noUserFeedback);

        FeedbackResponse result = feedbackService.submit(1L, new FeedbackRequest());

        assertThat(result.getUserId()).isNull();
        assertThat(result.getUsername()).isNull();
    }
}
