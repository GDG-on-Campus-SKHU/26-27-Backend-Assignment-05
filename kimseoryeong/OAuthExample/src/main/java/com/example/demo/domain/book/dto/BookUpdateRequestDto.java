package com.example.demo.domain.book.dto;

public record BookUpdateRequestDto(
        String title,
        Integer price
) {
}
