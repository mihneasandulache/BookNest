package com.booknest.service;

import com.booknest.dto.book.BookRequest;
import com.booknest.dto.book.BookResponse;
import com.booknest.entity.Author;
import com.booknest.entity.Book;
import com.booknest.entity.Genre;
import com.booknest.entity.Review;
import com.booknest.entity.User;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.repository.AuthorRepository;
import com.booknest.repository.BookRepository;
import com.booknest.repository.GenreRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private AuthorService authorService;
    @Mock
    private GenreService genreService;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("1984")
                .isbn("978-0451524935")
                .publishedYear(1949)
                .description("Dystopian novel")
                .coverImageUrl("http://example.com/cover.jpg")
                .authors(new java.util.HashSet<>())
                .genres(new java.util.HashSet<>())
                .reviews(new ArrayList<>())
                .build();
    }

    @Test
    void getAll_returnsPageOfBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(pageable)).thenReturn(page);

        Page<BookResponse> result = bookService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("1984");
    }

    @Test
    void search_returnsMatchingBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.search("1984", pageable)).thenReturn(page);

        Page<BookResponse> result = bookService.search("1984", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("1984");
    }

    @Test
    void getById_returnsBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse result = bookService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("1984");
        assertThat(result.getIsbn()).isEqualTo("978-0451524935");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsBook() {
        BookRequest req = new BookRequest();
        req.setTitle("Animal Farm");
        req.setIsbn("978-0451526342");
        req.setPublishedYear(1945);
        req.setDescription("Political allegory");
        req.setAuthorIds(null);
        req.setGenreIds(null);

        Book saved = Book.builder().id(2L).title("Animal Farm").isbn("978-0451526342")
                .publishedYear(1945).description("Political allegory")
                .authors(new java.util.HashSet<>()).genres(new java.util.HashSet<>())
                .reviews(new ArrayList<>()).build();
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookResponse result = bookService.create(req);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("Animal Farm");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void create_withAuthorAndGenreIds_resolvesRelations() {
        BookRequest req = new BookRequest();
        req.setTitle("Dune");
        req.setAuthorIds(Set.of(1L));
        req.setGenreIds(Set.of(2L));

        Author author = Author.builder().id(1L).firstName("Frank").lastName("Herbert").build();
        Genre genre = Genre.builder().id(2L).name("Sci-Fi").build();

        when(authorRepository.findAllById(Set.of(1L))).thenReturn(List.of(author));
        when(genreRepository.findAllById(Set.of(2L))).thenReturn(List.of(genre));

        Book saved = Book.builder().id(3L).title("Dune")
                .authors(Set.of(author)).genres(Set.of(genre))
                .reviews(new ArrayList<>()).build();
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookResponse result = bookService.create(req);

        assertThat(result.getTitle()).isEqualTo("Dune");
        verify(authorRepository).findAllById(Set.of(1L));
        verify(genreRepository).findAllById(Set.of(2L));
    }

    @Test
    void update_updatesAndReturnsBook_whenExists() {
        BookRequest req = new BookRequest();
        req.setTitle("Nineteen Eighty-Four");
        req.setIsbn("978-0451524935");
        req.setPublishedYear(1949);
        req.setAuthorIds(null);
        req.setGenreIds(null);

        Book updated = Book.builder().id(1L).title("Nineteen Eighty-Four")
                .authors(new java.util.HashSet<>()).genres(new java.util.HashSet<>())
                .reviews(new ArrayList<>()).build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updated);

        BookResponse result = bookService.update(1L, req);

        assertThat(result.getTitle()).isEqualTo("Nineteen Eighty-Four");
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(99L, new BookRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void toResponse_calculatesAverageRating_withReviews() {
        User user = User.builder().id(1L).username("alice").email("alice@example.com")
                .password("pass").build();
        Review r1 = Review.builder().id(1L).user(user).book(book).rating(4).build();
        Review r2 = Review.builder().id(2L).user(user).book(book).rating(2).build();
        book.setReviews(List.of(r1, r2));

        BookResponse response = bookService.toResponse(book);

        assertThat(response.getAverageRating()).isEqualTo(3.0);
        assertThat(response.getReviewCount()).isEqualTo(2);
    }

    @Test
    void toResponse_returnsZeroAverageRating_withNoReviews() {
        book.setReviews(new ArrayList<>());

        BookResponse response = bookService.toResponse(book);

        assertThat(response.getAverageRating()).isEqualTo(0.0);
        assertThat(response.getReviewCount()).isEqualTo(0);
    }

    @Test
    void toResponse_handlesNullAuthorsAndGenres() {
        book.setAuthors(null);
        book.setGenres(null);

        BookResponse response = bookService.toResponse(book);

        assertThat(response.getAuthors()).isEmpty();
        assertThat(response.getGenres()).isEmpty();
    }
}
