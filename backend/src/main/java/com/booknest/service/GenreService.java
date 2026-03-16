package com.booknest.service;

import com.booknest.dto.genre.GenreRequest;
import com.booknest.dto.genre.GenreResponse;
import com.booknest.entity.Genre;
import com.booknest.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<GenreResponse> getAll() {
        return genreRepository.findAll().stream().map(this::toResponse).toList();
    }

    public GenreResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public GenreResponse create(GenreRequest req) {
        Genre genre = Genre.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();
        return toResponse(genreRepository.save(genre));
    }

    public GenreResponse update(Long id, GenreRequest req) {
        Genre genre = findOrThrow(id);
        genre.setName(req.getName());
        genre.setDescription(req.getDescription());
        return toResponse(genreRepository.save(genre));
    }

    public void delete(Long id) {
        genreRepository.delete(findOrThrow(id));
    }

    private Genre findOrThrow(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Genre not found: " + id));
    }

    public GenreResponse toResponse(Genre genre) {
        GenreResponse r = new GenreResponse();
        r.setId(genre.getId());
        r.setName(genre.getName());
        r.setDescription(genre.getDescription());
        return r;
    }
}
