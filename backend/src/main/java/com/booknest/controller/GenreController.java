package com.booknest.controller;

import com.booknest.dto.genre.GenreRequest;
import com.booknest.dto.genre.GenreResponse;
import com.booknest.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreResponse>> getAll() {
        return ResponseEntity.ok(genreService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenreResponse> create(@Valid @RequestBody GenreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenreResponse> update(@PathVariable Long id, @Valid @RequestBody GenreRequest request) {
        return ResponseEntity.ok(genreService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        genreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
