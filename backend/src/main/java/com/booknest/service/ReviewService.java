package com.booknest.service;

import com.booknest.dto.review.ReviewRequest;
import com.booknest.dto.review.ReviewResponse;
import com.booknest.entity.Book;
import com.booknest.entity.Review;
import com.booknest.entity.User;
import com.booknest.repository.BookRepository;
import com.booknest.repository.ReviewRepository;
import com.booknest.repository.UserRepository;
import com.booknest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Page<ReviewResponse> getByBook(Long bookId, Pageable pageable) {
        return reviewRepository.findByBookId(bookId, pageable).map(this::toResponse);
    }

    public Page<ReviewResponse> getByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    public ReviewResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ReviewResponse create(Long userId, ReviewRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + req.getBookId()));

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(req.getRating())
                .content(req.getContent())
                .build();
        return toResponse(reviewRepository.save(review));
    }

    public ReviewResponse update(Long id, Long userId, ReviewRequest req) {
        Review review = findOrThrow(id);
        if (!review.getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("Not your review");
        }
        review.setRating(req.getRating());
        review.setContent(req.getContent());
        return toResponse(reviewRepository.save(review));
    }

    public void delete(Long id, Long userId, boolean isAdmin) {
        Review review = findOrThrow(id);
        if (!isAdmin && !review.getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("Not your review");
        }
        reviewRepository.delete(review);
    }

    private Review findOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));
    }

    private ReviewResponse toResponse(Review r) {
        ReviewResponse res = new ReviewResponse();
        res.setId(r.getId());
        res.setBookId(r.getBook().getId());
        res.setBookTitle(r.getBook().getTitle());
        res.setUserId(r.getUser().getId());
        res.setUsername(r.getUser().getUsername());
        res.setRating(r.getRating());
        res.setContent(r.getContent());
        res.setCreatedAt(r.getCreatedAt());
        res.setUpdatedAt(r.getUpdatedAt());
        return res;
    }
}
