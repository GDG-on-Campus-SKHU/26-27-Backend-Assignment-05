package com.example.demo.jpa.dto;

import com.example.demo.jpa.domain.Genre;

import java.util.List;

public record GenreResponse(Long id, String name, List<BookSummary> books) {
    public static GenreResponse from(Genre genre) {
        return new GenreResponse(genre.getId(), genre.getName(),
                genre.getBooks().stream().map(BookSummary::from).toList());
    }
}
