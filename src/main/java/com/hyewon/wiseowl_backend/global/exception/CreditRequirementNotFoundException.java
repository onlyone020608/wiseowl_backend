package com.hyewon.wiseowl_backend.global.exception;

import com.hyewon.wiseowl_backend.domain.requirement.entity.MajorType;
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
    public CreditRequirementNotFoundException(Long majorId, MajorType majorType) {
        super("Credit requirement not found with major Id: " + majorId + "majorType: " + majorType);
    }
    public CreditRequirementNotFoundException() {
        super("Credit requirement not found.");
    }
}
