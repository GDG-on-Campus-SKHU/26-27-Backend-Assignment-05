package com.example.oauthexample.domain.project.repository;

import com.example.oauthexample.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByUser_Id(Long userId);
    Optional<Project> findByIdAndUser_Id(Long projectId, Long userId);
}
