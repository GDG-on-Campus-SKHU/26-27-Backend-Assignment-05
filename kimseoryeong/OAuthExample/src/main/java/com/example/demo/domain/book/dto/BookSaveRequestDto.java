package com.example.demo.domain.book.dto;

public record BookSaveRequestDto(
        String title,
        Integer price,
        String author
) {
}
