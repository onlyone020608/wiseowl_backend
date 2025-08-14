package com.hyewon.wiseowl_backend.global.exception;

import lombok.Getter;

@Getter
public class RequiredLiberalCategoryNotFoundException extends RuntimeException {
    private final ErrorCode errorCode = ErrorCode.REQUIRED_LIBERAL_CATEGORY_NOT_FOUND;

    public RequiredLiberalCategoryNotFoundException(String message) {
        super(message);
    }
    public RequiredLiberalCategoryNotFoundException(){
        super("Required liberal category not found.");
    }
    public RequiredLiberalCategoryNotFoundException(Long requiredLiberalCategoryId){
        super("Required liberal category not found with id: " + requiredLiberalCategoryId);
    }
}
