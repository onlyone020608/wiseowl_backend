package com.hyewon.wiseowl_backend.global.exception;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
import com.hyewon.wiseowl_backend.domain.requirement.entity.Track;
import lombok.Getter;

@Getter
public class CreditRequirementNotFoundException extends RuntimeException {
    private final ErrorCode errorCode =  ErrorCode.CREDIT_REQUIREMENT_NOT_FOUND;

    public CreditRequirementNotFoundException(String message) {
        super(message);
    }
    public CreditRequirementNotFoundException(Long majorId) {
        super("Credit requirement not found with major Id: " + majorId);
    }
    public CreditRequirementNotFoundException(Long majorId, MajorType majorType, Track track) {
        super("Credit requirement not found with major Id: " + majorId + "majorType: " + majorType + "track" + track);
    }
    public CreditRequirementNotFoundException() {
        super("Credit requirement not found.");
    }
}
