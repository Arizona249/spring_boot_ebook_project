package com.ebookApi.EBook.DTO.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDTO(Instant timestamp,
                               int status,
                               String error,
                               String message,
                               String path,
                               List<?> details) {
}
