package com.hyewon.wiseowl_backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer entranceYear;

    private boolean adConsent;

    private Double gpa;

    public void updateEntranceYear(Integer entranceYear) {
        this.entranceYear = entranceYear;
    }

    public void assignUser(User user) {
        this.user = user;
    }

    public static Profile createDefault() {
        return new Profile();
    }

    public void updateGpa(double gpa){
        this.gpa = gpa;
    }
}
