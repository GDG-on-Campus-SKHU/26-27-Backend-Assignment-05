package com.example.demo.domain.book.dto;

import com.example.demo.domain.book.domain.Genre;

public record BookSaveRequestDto(
        Long authorId,
        String title,
        Genre genre
) {
}
