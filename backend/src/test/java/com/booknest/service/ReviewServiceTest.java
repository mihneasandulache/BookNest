package com.booknest.service;

import com.booknest.dto.review.ReviewRequest;
import com.booknest.dto.review.ReviewResponse;
import com.booknest.entity.Book;
import com.booknest.entity.Review;
import com.booknest.entity.User;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.repository.BookRepository;
import com.booknest.repository.ReviewRepository;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Book book;
    private Review review;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("encoded")
                .build();

        book = Book.builder()
                .id(10L)
                .title("1984")
                .build();

        review = Review.builder()
                .id(100L)
                .user(user)
                .book(book)
                .rating(5)
                .content("Excellent book!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getByBook_returnsPageOfReviews() {
        Pageable pageable = PageRequest.of(0, 10);
        when(reviewRepository.findByBookId(10L, pageable)).thenReturn(new PageImpl<>(List.of(review)));

        Page<ReviewResponse> result = reviewService.getByBook(10L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBookId()).isEqualTo(10L);
        assertThat(result.getContent().get(0).getRating()).isEqualTo(5);
    }

    @Test
    void getByUser_returnsPageOfReviews() {
        Pageable pageable = PageRequest.of(0, 10);
        when(reviewRepository.findByUserId(1L, pageable)).thenReturn(new PageImpl<>(List.of(review)));

        Page<ReviewResponse> result = reviewService.getByUser(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void getById_returnsReview_whenExists() {
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        ReviewResponse result = reviewService.getById(100L);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getContent()).isEqualTo("Excellent book!");
        assertThat(result.getBookTitle()).isEqualTo("1984");
        assertThat(result.getUsername()).isEqualTo("alice");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_savesAndReturnsReview() {
        ReviewRequest req = new ReviewRequest();
        req.setBookId(10L);
        req.setRating(4);
        req.setContent("Great read");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponse result = reviewService.create(1L, req);

        assertThat(result.getId()).isEqualTo(100L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void create_throwsResourceNotFoundException_whenUserNotFound() {
        ReviewRequest req = new ReviewRequest();
        req.setBookId(10L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(99L, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void create_throwsResourceNotFoundException_whenBookNotFound() {
        ReviewRequest req = new ReviewRequest();
        req.setBookId(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(1L, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found");
    }

    @Test
    void update_updatesReview_whenOwner() {
        ReviewRequest req = new ReviewRequest();
        req.setBookId(10L);
        req.setRating(3);
        req.setContent("Updated content");

        Review updated = Review.builder().id(100L).user(user).book(book)
                .rating(3).content("Updated content").build();
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(updated);

        ReviewResponse result = reviewService.update(100L, 1L, req);

        assertThat(result.getRating()).isEqualTo(3);
        assertThat(result.getContent()).isEqualTo("Updated content");
    }

    @Test
    void update_throwsAccessDeniedException_whenNotOwner() {
        ReviewRequest req = new ReviewRequest();
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.update(100L, 99L, req))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void update_throwsResourceNotFoundException_whenReviewNotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.update(999L, 1L, new ReviewRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesReview_whenOwner() {
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        reviewService.delete(100L, 1L, false);

        verify(reviewRepository).delete(review);
    }

    @Test
    void delete_deletesReview_whenAdmin() {
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        reviewService.delete(100L, 99L, true);

        verify(reviewRepository).delete(review);
    }

    @Test
    void delete_throwsAccessDeniedException_whenNotOwnerAndNotAdmin() {
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.delete(100L, 99L, false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenReviewNotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.delete(999L, 1L, false))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
