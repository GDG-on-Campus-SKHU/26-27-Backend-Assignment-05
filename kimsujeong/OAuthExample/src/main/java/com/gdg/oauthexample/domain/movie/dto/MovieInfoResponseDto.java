package com.gdg.oauthexample.domain.movie.dto;

import com.gdg.oauthexample.domain.movie.entity.Movie;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MovieInfoResponseDto(
        Long movieId,
        String title,
        String director,
        LocalDate releaseDate,
        Long userId
) {
    public static MovieInfoResponseDto from(Movie movie) {
        return MovieInfoResponseDto.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .director(movie.getDirector())
                .releaseDate(movie.getReleaseDate())
                .userId(movie.getUser().getId())
                .build();
    }
}
