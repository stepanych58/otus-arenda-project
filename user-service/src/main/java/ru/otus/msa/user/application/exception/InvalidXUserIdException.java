package ru.otus.msa.user.application.exception;

public class InvalidXUserIdException extends RuntimeException {
    public InvalidXUserIdException(String message) {
        super(message);
    }
}
