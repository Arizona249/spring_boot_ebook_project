package com.ebookApi.EBook.exception;

public class ApiResourceNotFoundException extends RuntimeException{
    public ApiResourceNotFoundException(String message){
        super(message);
    }
}
