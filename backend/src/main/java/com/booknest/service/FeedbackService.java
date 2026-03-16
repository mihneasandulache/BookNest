package com.booknest.service;

import com.booknest.dto.feedback.FeedbackRequest;
import com.booknest.dto.feedback.FeedbackResponse;
import com.booknest.entity.Feedback;
import com.booknest.entity.User;
import com.booknest.repository.FeedbackRepository;
import com.booknest.repository.UserRepository;
import com.booknest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public FeedbackResponse submit(Long userId, FeedbackRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Feedback feedback = Feedback.builder()
                .user(user)
                .category(req.getCategory())
                .contactMethod(req.getContactMethod())
                .subscribeNewsletter(req.isSubscribeNewsletter())
                .message(req.getMessage())
                .build();

        Feedback saved = feedbackRepository.save(feedback);
        emailService.sendFeedbackConfirmationEmail(user.getEmail(), user.getUsername());
        return toResponse(saved);
    }

    public Page<FeedbackResponse> getAll(Pageable pageable) {
        return feedbackRepository.findAll(pageable).map(this::toResponse);
    }

    public void delete(Long id) {
        feedbackRepository.delete(feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found: " + id)));
    }

    private FeedbackResponse toResponse(Feedback f) {
        FeedbackResponse res = new FeedbackResponse();
        res.setId(f.getId());
        res.setUserId(f.getUser() != null ? f.getUser().getId() : null);
        res.setUsername(f.getUser() != null ? f.getUser().getUsername() : null);
        res.setCategory(f.getCategory());
        res.setContactMethod(f.getContactMethod());
        res.setSubscribeNewsletter(f.isSubscribeNewsletter());
        res.setMessage(f.getMessage());
        res.setSubmittedAt(f.getSubmittedAt());
        return res;
    }
}
