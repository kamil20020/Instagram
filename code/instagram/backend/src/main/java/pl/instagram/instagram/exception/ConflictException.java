package pl.instagram.instagram.exception;

public class ConflictException extends RuntimeException{

    public ConflictException(String message) {
        super(message);
    }
}
