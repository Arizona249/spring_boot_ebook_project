package com.ebookApi.EBook.DTO;

import com.ebookApi.EBook.DTO.response.ApiResponse;
import com.ebookApi.EBook.DTO.response.ErrorResponseDTO;
import com.ebookApi.EBook.enums.HttpConstants;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public class ApiResponseFactory {

    private static final String DEFAULT_SUCCESS_MESSAGE="Request Successful";

    public static  <T> ApiResponse<T> createSuccessResponse(T payload,String message){
        if(message==null)message=DEFAULT_SUCCESS_MESSAGE;
        return new ApiResponse<>(HttpConstants.SUCCESS.value,message,payload);
    }
    public static  ApiResponse<String> createSuccessResponse(String payload){
        return new ApiResponse<>(HttpConstants.SUCCESS.value,DEFAULT_SUCCESS_MESSAGE,payload);
    }
    public static  ApiResponse<String> createErrorResponse(String message, HttpStatus status){
        if(message==null)message=DEFAULT_SUCCESS_MESSAGE;
        return new ApiResponse<>(HttpConstants.ERROR.value,message,"Error: "+status.getReasonPhrase());
    }

    public static ErrorResponseDTO createErrorResponse(HttpStatus status, String path, String message, List<?> details){
        return new ErrorResponseDTO(Instant.now(),status.value(), status.getReasonPhrase(),message,path,details);
    }


}
