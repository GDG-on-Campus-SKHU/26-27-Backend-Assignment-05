package com.example.demo.jpa.repository;

import com.example.demo.jpa.domain.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Override
    @EntityGraph(attributePaths = "genre")
    @Query("select b from Book b order by b.id")
    List<Book> findAll();

    @Override
    @EntityGraph(attributePaths = "genre")
    Optional<Book> findById(Long id);

    boolean existsByGenreId(Long genreId);
}
