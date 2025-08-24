package com.hyewon.wiseowl_backend.fixture;

import com.hyewon.wiseowl_backend.domain.user.entity.Profile;
import com.hyewon.wiseowl_backend.domain.user.entity.User;

public class UserFixture {

    public static User aDefaultUser() {
        return User.builder()
                .id(1L)
                .username("Test")
                .email("tester@email.com")
                .password("encodedPassword")
                .build();
    }

    public static User aUserWithProfile(Profile profile) {
        return User.builder()
                .id(1L)
                .username("Test")
                .profile(profile)
                .email("test@test.com")
                .build();
    }
}
