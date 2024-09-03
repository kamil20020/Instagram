package pl.instagram.instagram.exception;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public record ExceptionResponse(
    HttpStatus httpStatus,
    String message,
    OffsetDateTime timestamp
) {}
