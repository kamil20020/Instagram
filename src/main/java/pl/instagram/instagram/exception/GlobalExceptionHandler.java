package pl.instagram.instagram.exception;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException e){

        HttpStatus status = HttpStatus.NOT_FOUND;

        ExceptionResponse response = new ExceptionResponse(
            status,
            e.getMessage(),
            OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleGotIllegalArgumentsException(IllegalArgumentException e){

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ExceptionResponse response = new ExceptionResponse(
                status,
                e.getMessage(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<FormExceptionResponse> handleFormException(MethodArgumentNotValidException e){

        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors()
            .forEach(fieldError -> {

                String fieldName = fieldError.getField();
                String fieldMessage = fieldError.getDefaultMessage();

                errors.put(fieldName, fieldMessage);
            });

        FormExceptionResponse response = new FormExceptionResponse(
            status,
            "Podano niepoprawne dane",
            errors,
            OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(value = {ConflictException.class, EntityExistsException.class})
    public ResponseEntity<ExceptionResponse> handleConflictException(RuntimeException e){

        HttpStatus status = HttpStatus.CONFLICT;

        ExceptionResponse response = new ExceptionResponse(
                status,
                e.getMessage(),
                OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> defaultExceptionHandler(Exception e){

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionResponse response = new ExceptionResponse(
            status,
            e.getMessage(),
            OffsetDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }
}
