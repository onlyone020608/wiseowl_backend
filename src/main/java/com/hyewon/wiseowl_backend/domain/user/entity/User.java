package com.hyewon.wiseowl_backend.domain.user.entity;

import com.hyewon.wiseowl_backend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @NotBlank
    private String email;

    private String password;

    private boolean deleted;

    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    public void updateUsername(String username){
        this.username = username;
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
    }

    public void assignProfile(Profile profile) {
        this.profile = profile;
        profile.assignUser(this);
    }

    private User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static User of(String email, String password) {
        return new User(email, password);
    }
}
