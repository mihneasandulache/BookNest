package com.booknest.service;

import com.booknest.dto.genre.GenreRequest;
import com.booknest.dto.genre.GenreResponse;
import com.booknest.entity.Genre;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = Genre.builder()
                .id(1L)
                .name("Fiction")
                .description("Fictional works")
                .build();
    }

    @Test
    void getAll_returnsAllGenres() {
        when(genreRepository.findAll()).thenReturn(List.of(genre));

        List<GenreResponse> result = genreService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Fiction");
    }

    @Test
    void getAll_returnsEmptyList_whenNoGenres() {
        when(genreRepository.findAll()).thenReturn(List.of());

        List<GenreResponse> result = genreService.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getById_returnsGenre_whenExists() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        GenreResponse result = genreService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Fiction");
        assertThat(result.getDescription()).isEqualTo("Fictional works");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsGenre() {
        GenreRequest req = new GenreRequest();
        req.setName("Mystery");
        req.setDescription("Mystery novels");

        Genre saved = Genre.builder().id(2L).name("Mystery").description("Mystery novels").build();
        when(genreRepository.save(any(Genre.class))).thenReturn(saved);

        GenreResponse result = genreService.create(req);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Mystery");
        verify(genreRepository).save(any(Genre.class));
    }

    @Test
    void update_updatesAndReturnsGenre_whenExists() {
        GenreRequest req = new GenreRequest();
        req.setName("Science Fiction");
        req.setDescription("Sci-fi works");

        Genre updated = Genre.builder().id(1L).name("Science Fiction").description("Sci-fi works").build();
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.save(any(Genre.class))).thenReturn(updated);

        GenreResponse result = genreService.update(1L, req);

        assertThat(result.getName()).isEqualTo("Science Fiction");
        assertThat(result.getDescription()).isEqualTo("Sci-fi works");
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.update(99L, new GenreRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesGenre_whenExists() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        genreService.delete(1L);

        verify(genreRepository).delete(genre);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void toResponse_mapsAllFields() {
        GenreResponse response = genreService.toResponse(genre);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Fiction");
        assertThat(response.getDescription()).isEqualTo("Fictional works");
    }
}
