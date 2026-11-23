package com.example.demo.domain.movie.dto;

import jakarta.validation.constraints.NotBlank;

public record MovieSaveRequestDto(
        @NotBlank(message = "영화 제목을 입력해주세요.")
        String title,
        String editor
) {
}
