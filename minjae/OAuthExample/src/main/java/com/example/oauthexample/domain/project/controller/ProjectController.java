package com.example.oauthexample.domain.project.controller;

import com.example.oauthexample.domain.project.dto.ProjectCreateRequestDto;
import com.example.oauthexample.domain.project.dto.ProjectRenameRequestDto;
import com.example.oauthexample.domain.project.dto.ProjectResponseDto;
import com.example.oauthexample.domain.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProjectCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getMyProjects(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(projectService.getMyProjects(userId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> getMyProject(@AuthenticationPrincipal Long userId,
                                                         @PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getMyProject(userId, projectId));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> renameMyProject(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRenameRequestDto request
    ) {
        return ResponseEntity.ok(projectService.renameMyProject(userId, projectId, request));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteMyProject(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long projectId) {
        projectService.deleteMyProject(userId, projectId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
