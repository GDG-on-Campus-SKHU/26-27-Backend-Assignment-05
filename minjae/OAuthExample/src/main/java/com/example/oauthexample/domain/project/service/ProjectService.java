package com.example.oauthexample.domain.project.service;

import com.example.oauthexample.domain.project.dto.ProjectCreateRequestDto;
import com.example.oauthexample.domain.project.dto.ProjectRenameRequestDto;
import com.example.oauthexample.domain.project.dto.ProjectResponseDto;
import com.example.oauthexample.domain.project.entity.Project;
import com.example.oauthexample.domain.project.repository.ProjectRepository;
import com.example.oauthexample.domain.user.entity.User;
import com.example.oauthexample.domain.user.repository.UserRepository;
import com.example.oauthexample.global.exception.ErrorCode;
import com.example.oauthexample.global.exception.ForbiddenException;
import com.example.oauthexample.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponseDto createProject(Long userId, ProjectCreateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Project project = Project.builder()
                .projectTitle(request.projectTitle())
                .user(user)
                .build();

        return ProjectResponseDto.from(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getMyProjects(Long userId) {
        return projectRepository.findAllByUser_Id(userId).stream()
                .map(ProjectResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponseDto getMyProject(Long userId, Long projectId) {
        Project project = getOwnedProjectOrThrow(userId, projectId);

        return ProjectResponseDto.from(project);
    }

    @Transactional
    public ProjectResponseDto renameMyProject(Long userId, Long projectId, ProjectRenameRequestDto request) {
        Project project = getOwnedProjectOrThrow(userId, projectId);

        project.rename(request.projectTitle());

        return ProjectResponseDto.from(project);
    }

    @Transactional
    public void deleteMyProject(Long userId, Long projectId) {
        Project project = getOwnedProjectOrThrow(userId, projectId);

        projectRepository.delete(project);
    }

    private Project getOwnedProjectOrThrow(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.PROJECT_FORBIDDEN);
        }
        return project;
    }
}
