package com.hyewon.wiseowl_backend.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SignUpRequest {
    private final String email;
    private final String password;

    @JsonCreator
    public SignUpRequest(@JsonProperty("email") String email,
                         @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;

    }




}
