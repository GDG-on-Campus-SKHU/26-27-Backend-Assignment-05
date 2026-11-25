package com.example.oauthexample.domain.project.dto;

import com.example.oauthexample.domain.project.entity.Project;
import lombok.Builder;

@Builder
public record ProjectResponseDto(
        Long id,
        String projectTitle
) {
    public static ProjectResponseDto from(Project project) {
        return ProjectResponseDto.builder()
                .id(project.getId())
                .projectTitle(project.getProjectTitle())
                .build();
    }
}
