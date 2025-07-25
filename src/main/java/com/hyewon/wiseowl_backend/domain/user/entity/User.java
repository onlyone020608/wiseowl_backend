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

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String studentId;

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

    public void markAsDeleted() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    private User(String email, String password) {

        this.email = email;
        this.password = password;
    }

    public static User of(String email, String password) {
        return new User(email, password);
    }

}
