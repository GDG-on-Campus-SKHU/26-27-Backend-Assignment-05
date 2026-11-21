package com.example.demo.domain.book.dto;

import com.example.demo.domain.book.entity.Book;
import lombok.Builder;

@Builder
public record BookInfoResponseDto(
        Long id,
        String title,
        int price,
        String author
) {

    public static BookInfoResponseDto from(Book book) {
        return BookInfoResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .price(book.getPrice())
                .author(book.getAuthor())
                .build();
    }
}
