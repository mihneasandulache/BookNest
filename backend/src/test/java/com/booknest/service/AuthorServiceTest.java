package com.booknest.service;

import com.booknest.dto.author.AuthorRequest;
import com.booknest.dto.author.AuthorResponse;
import com.booknest.entity.Author;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.repository.AuthorRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author;

    @BeforeEach
    void setUp() {
        author = Author.builder()
                .id(1L)
                .firstName("George")
                .lastName("Orwell")
                .bio("British author")
                .nationality("British")
                .build();
    }

    @Test
    void getAll_returnsPageOfAuthors() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Author> page = new PageImpl<>(List.of(author));
        when(authorRepository.findAll(pageable)).thenReturn(page);

        Page<AuthorResponse> result = authorService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("George");
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Orwell");
    }

    @Test
    void search_returnsMatchingAuthors() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Author> page = new PageImpl<>(List.of(author));
        when(authorRepository.search("George", pageable)).thenReturn(page);

        Page<AuthorResponse> result = authorService.search("George", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("George");
    }

    @Test
    void getById_returnsAuthor_whenExists() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorResponse result = authorService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("George");
        assertThat(result.getNationality()).isEqualTo("British");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsAuthor() {
        AuthorRequest req = new AuthorRequest();
        req.setFirstName("Franz");
        req.setLastName("Kafka");
        req.setBio("Czech author");
        req.setNationality("Czech");

        Author saved = Author.builder().id(2L).firstName("Franz").lastName("Kafka")
                .bio("Czech author").nationality("Czech").build();
        when(authorRepository.save(any(Author.class))).thenReturn(saved);

        AuthorResponse result = authorService.create(req);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getFirstName()).isEqualTo("Franz");
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void update_updatesAndReturnsAuthor_whenExists() {
        AuthorRequest req = new AuthorRequest();
        req.setFirstName("Eric");
        req.setLastName("Blair");
        req.setBio("Pen name: George Orwell");
        req.setNationality("British");

        Author updated = Author.builder().id(1L).firstName("Eric").lastName("Blair")
                .bio("Pen name: George Orwell").nationality("British").build();
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(updated);

        AuthorResponse result = authorService.update(1L, req);

        assertThat(result.getFirstName()).isEqualTo("Eric");
        assertThat(result.getLastName()).isEqualTo("Blair");
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.update(99L, new AuthorRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesAuthor_whenExists() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        authorService.delete(1L);

        verify(authorRepository).delete(author);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void toResponse_mapsAllFields() {
        AuthorResponse response = authorService.toResponse(author);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("George");
        assertThat(response.getLastName()).isEqualTo("Orwell");
        assertThat(response.getBio()).isEqualTo("British author");
        assertThat(response.getNationality()).isEqualTo("British");
    }
}
