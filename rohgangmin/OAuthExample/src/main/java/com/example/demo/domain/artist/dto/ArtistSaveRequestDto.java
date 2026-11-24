package com.example.demo.domain.artist.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtistSaveRequestDto(
        @NotBlank(message = "아티스트 활동명(stageName)은 필수입니다.")
        String stageName,

        @NotBlank(message = "국적(nationality)은 필수입니다.")
        String nationality,

        @NotNull(message = "데뷔 연도(debutYear)는 필수입니다.")
        Integer debutYear
) {
}
