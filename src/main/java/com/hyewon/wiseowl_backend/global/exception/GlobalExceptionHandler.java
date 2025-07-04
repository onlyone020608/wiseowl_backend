package com.hyewon.wiseowl_backend.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            CourseNotFoundException.class,
            LiberalCategoryNotFoundException.class,
            ProfileNotFoundException.class,
            UserNotFoundException.class,
            MajorNotFoundException.class
    })
    public ResponseEntity<CustomErrorResponse> handleDomainExceptions(RuntimeException ex) {
        if (ex instanceof CourseNotFoundException e) {
            return buildResponse(e.getErrorCode());
        } else if (ex instanceof LiberalCategoryNotFoundException e) {
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof ProfileNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof UserNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof MajorNotFoundException e){
            return buildResponse(e.getErrorCode());
        }

        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleException(Exception ex) {
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<CustomErrorResponse> buildResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(new CustomErrorResponse(errorCode));
    }


}
