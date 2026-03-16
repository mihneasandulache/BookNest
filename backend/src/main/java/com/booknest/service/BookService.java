package com.booknest.service;

import com.booknest.dto.book.BookRequest;
import com.booknest.dto.book.BookResponse;
import com.booknest.entity.Author;
import com.booknest.entity.Book;
import com.booknest.entity.Genre;
import com.booknest.repository.AuthorRepository;
import com.booknest.repository.BookRepository;
import com.booknest.repository.GenreRepository;
import com.booknest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final AuthorService authorService;
    private final GenreService genreService;

    public Page<BookResponse> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<BookResponse> search(String query, Pageable pageable) {
        return bookRepository.search(query, pageable).map(this::toResponse);
    }

    public BookResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public BookResponse create(BookRequest req) {
        Book book = Book.builder()
                .title(req.getTitle())
                .isbn(req.getIsbn())
                .publishedYear(req.getPublishedYear())
                .description(req.getDescription())
                .coverImageUrl(req.getCoverImageUrl())
                .authors(resolveAuthors(req.getAuthorIds()))
                .genres(resolveGenres(req.getGenreIds()))
                .build();
        return toResponse(bookRepository.save(book));
    }

    public BookResponse update(Long id, BookRequest req) {
        Book book = findOrThrow(id);
        book.setTitle(req.getTitle());
        book.setIsbn(req.getIsbn());
        book.setPublishedYear(req.getPublishedYear());
        book.setDescription(req.getDescription());
        book.setCoverImageUrl(req.getCoverImageUrl());
        book.setAuthors(resolveAuthors(req.getAuthorIds()));
        book.setGenres(resolveGenres(req.getGenreIds()));
        return toResponse(bookRepository.save(book));
    }

    public void delete(Long id) {
        bookRepository.delete(findOrThrow(id));
    }

    private Book findOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    private Set<Author> resolveAuthors(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(authorRepository.findAllById(ids));
    }

    private Set<Genre> resolveGenres(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(genreRepository.findAllById(ids));
    }

    public BookResponse toResponse(Book book) {
        BookResponse r = new BookResponse();
        r.setId(book.getId());
        r.setTitle(book.getTitle());
        r.setIsbn(book.getIsbn());
        r.setPublishedYear(book.getPublishedYear());
        r.setDescription(book.getDescription());
        r.setCoverImageUrl(book.getCoverImageUrl());
        r.setAuthors(book.getAuthors() == null ? Set.of() :
                book.getAuthors().stream().map(authorService::toResponse).collect(java.util.stream.Collectors.toSet()));
        r.setGenres(book.getGenres() == null ? Set.of() :
                book.getGenres().stream().map(genreService::toResponse).collect(java.util.stream.Collectors.toSet()));
        double avg = book.getReviews() == null || book.getReviews().isEmpty() ? 0.0 :
                book.getReviews().stream().mapToInt(rv -> rv.getRating()).average().orElse(0.0);
        r.setAverageRating(avg);
        r.setReviewCount(book.getReviews() == null ? 0 : book.getReviews().size());
        return r;
    }
}
