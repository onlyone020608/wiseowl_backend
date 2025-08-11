package com.hyewon.wiseowl_backend.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            CourseNotFoundException.class,
            LiberalCategoryNotFoundException.class,
            ProfileNotFoundException.class,
            UserNotFoundException.class,
            MajorNotFoundException.class,
            EmailAlreadyExistsException.class,
            CompletedCourseAlreadyExistsException.class,
            CourseOfferingNotFoundException.class,
            UserRequirementStatusNotFoundException.class,
            UserMajorNotFoundException.class,
            CreditRequirementNotFoundException.class,
            RequiredMajorCourseNotFoundException.class,
            RequiredLiberalCategoryNotFoundException.class,
            UserRequiredCourseStatusNotFoundException.class,
            UserCompletedCourseNotFoundException.class,
            FacilityNotFoundException.class,
            OrganizationNotFoundException.class,
            SemesterNotFoundException.class

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
        } else if(ex instanceof EmailAlreadyExistsException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof CompletedCourseAlreadyExistsException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof CourseOfferingNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof UserRequirementStatusNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof UserMajorNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof CreditRequirementNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof RequiredMajorCourseNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof RequiredLiberalCategoryNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof UserRequiredCourseStatusNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof UserCompletedCourseNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof  FacilityNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof OrganizationNotFoundException e){
            return buildResponse(e.getErrorCode());
        } else if(ex instanceof SemesterNotFoundException e){
            return buildResponse(e.getErrorCode());
        }

        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            InvalidCurrentPasswordException.class,
    })
    public ResponseEntity<CustomErrorResponse> handleBusinessExceptions(RuntimeException ex) {
        if (ex instanceof InvalidCurrentPasswordException e) {
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
