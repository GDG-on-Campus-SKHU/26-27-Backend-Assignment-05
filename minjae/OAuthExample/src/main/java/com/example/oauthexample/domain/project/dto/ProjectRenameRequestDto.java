package com.example.oauthexample.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRenameRequestDto(
        @NotBlank(message = "제목은 비어있을 수 없습니다.")
        @Size(max = 255, message = "제목은 255자 이하로 입력해주세요.")
        String projectTitle
) {
}
