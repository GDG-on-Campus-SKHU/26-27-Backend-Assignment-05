
package com.group.oauth.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(unique = true)
    private String googleId;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static User createGoogleUser(
            String email, String googleId) {
        User user = new User();
        user.email = email;
        user.googleId = googleId;
        return user;
    }
}
