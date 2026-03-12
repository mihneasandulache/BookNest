package com.booknest.repository;

import com.booknest.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.authors a " +
           "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Book> search(@Param("q") String query, Pageable pageable);
}
