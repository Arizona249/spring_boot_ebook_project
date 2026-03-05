package com.ebookApi.EBook.exception;

import com.ebookApi.EBook.DTO.ApiResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<?> handleInternalServerError(InternalServerError ex, HttpServletRequest request){
        var message=ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseFactory.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,request.getRequestURI(),message,new ArrayList<>()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex ,HttpServletRequest request){
        var message=ex.getMessage();
        if(message==null|| message.isBlank())message="Something Went Wrong, Please Try Again.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseFactory.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,request.getRequestURI(),message,new ArrayList<>()));
    }
    @ExceptionHandler(ApiResourceNotFoundException.class)
    public ResponseEntity<?> handleApiResourceNotFoundException(ApiResourceNotFoundException ex,HttpServletRequest request){
        var message =ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND,request.getRequestURI(),
                        message,new ArrayList<>()));
    }


}
