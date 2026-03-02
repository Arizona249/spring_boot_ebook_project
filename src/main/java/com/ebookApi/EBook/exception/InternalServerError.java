package com.ebookApi.EBook.exception;

public class InternalServerError extends RuntimeException {
    public InternalServerError(String message){
        super(message);
    }
}
