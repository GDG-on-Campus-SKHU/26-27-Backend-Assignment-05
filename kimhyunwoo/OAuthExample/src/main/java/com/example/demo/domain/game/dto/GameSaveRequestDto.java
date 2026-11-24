package com.example.demo.domain.game.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GameSaveRequestDto(
        @NotBlank(message = "게임 이름은 비어있을 수 없습니다.")
        @Size(max = 255, message = "게임 이름은 255자 이하로 입력해주세요.")
        String gameName,

        @Size(max = 255, message = "장르는 255자 이하로 입력해주세요.")
        String genre
) {
}
