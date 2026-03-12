package com.booknest.repository;

import com.booknest.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a WHERE " +
           "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Author> search(@Param("q") String query, Pageable pageable);
}
