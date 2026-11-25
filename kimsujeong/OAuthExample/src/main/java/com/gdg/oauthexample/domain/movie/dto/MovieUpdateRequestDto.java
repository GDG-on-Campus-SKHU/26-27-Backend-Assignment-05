package com.gdg.oauthexample.domain.movie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record MovieUpdateRequestDto(
    String title,

    String director,

    @PastOrPresent(message = "개봉일은 현재 또는 과거 날짜여야 합니다.")
    LocalDate releaseDate
) {
}
