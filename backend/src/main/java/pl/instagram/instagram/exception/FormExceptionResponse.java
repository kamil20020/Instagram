package pl.instagram.instagram.exception;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.Map;

public record FormExceptionResponse(
    HttpStatus httpStatus,
    String message,
    Map<String, String> errors,
    OffsetDateTime timestamp
){}
