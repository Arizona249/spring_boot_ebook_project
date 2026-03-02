package com.ebookApi.EBook.DTO.response;

public record ApiResponse<T>(
        String status,
        String message,
        T payload
) {
}
