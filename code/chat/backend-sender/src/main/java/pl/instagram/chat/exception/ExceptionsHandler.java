package pl.instagram.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<String> handleInvalidId(RuntimeException e){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid id was given");
    }

    @ExceptionHandler(value = {UserIsNotLoggedException.class})
    public ResponseEntity<Void> handleNotLoggedUser(RuntimeException e){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidForm(MethodArgumentNotValidException e){

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
