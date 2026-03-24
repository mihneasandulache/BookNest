package com.booknest.service;

import com.booknest.dto.author.AuthorRequest;
import com.booknest.dto.author.AuthorResponse;
import com.booknest.entity.Author;
import com.booknest.exception.ResourceNotFoundException;
import com.booknest.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public Page<AuthorResponse> getAll(Pageable pageable) {
        return authorRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<AuthorResponse> search(String q, Pageable pageable) {
        return authorRepository.search(q, pageable).map(this::toResponse);
    }

    public AuthorResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public AuthorResponse create(AuthorRequest req) {
        Author author = Author.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .bio(req.getBio())
                .nationality(req.getNationality())
                .build();
        return toResponse(authorRepository.save(author));
    }

    public AuthorResponse update(Long id, AuthorRequest req) {
        Author author = findOrThrow(id);
        author.setFirstName(req.getFirstName());
        author.setLastName(req.getLastName());
        author.setBio(req.getBio());
        author.setNationality(req.getNationality());
        return toResponse(authorRepository.save(author));
    }

    public void delete(Long id) {
        authorRepository.delete(findOrThrow(id));
    }

    private Author findOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
    }

    public AuthorResponse toResponse(Author author) {
        AuthorResponse r = new AuthorResponse();
        r.setId(author.getId());
        r.setFirstName(author.getFirstName());
        r.setLastName(author.getLastName());
        r.setBio(author.getBio());
        r.setNationality(author.getNationality());
        return r;
    }
}
