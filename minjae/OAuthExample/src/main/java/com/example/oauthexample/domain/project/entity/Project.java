package com.example.oauthexample.domain.project.entity;

import com.example.oauthexample.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Project(String projectTitle, User user) {
        this.user = Objects.requireNonNull(user, "프로젝트 소유자는 필수입니다.");
        this.projectTitle = projectTitle;
        // 연관관계의 주인과 반대쪽 컬렉션을 함께 설정합니다.
        user.getProjects().add(this);
    }

    public void rename(String projectTitle) {
        this.projectTitle = projectTitle;
    }
}
