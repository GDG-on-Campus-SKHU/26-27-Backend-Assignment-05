package com.example.demo.domain.movie.dto;

import com.example.demo.domain.movie.entity.Movie;
import lombok.Builder;

@Builder
public record MovieInfoResponseDto(
        Long id,
        String title,
        String editor
) {

    public static MovieInfoResponseDto from(Movie movie) {
        return MovieInfoResponseDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .editor(movie.getEditor())
                .build();
    }
}
