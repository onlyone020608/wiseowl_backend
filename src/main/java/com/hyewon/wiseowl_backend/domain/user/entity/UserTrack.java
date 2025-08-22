package com.hyewon.wiseowl_backend.domain.user.entity;

import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Track track;

    private UserTrack(User user, Track track) {
        this.user = user;
        this.track = track;
    }

    public static UserTrack of(User user, Track track) {
        return new UserTrack(user, track);
    }

    public void updateTrack(Track track) {
        this.track = track;
    }
}
