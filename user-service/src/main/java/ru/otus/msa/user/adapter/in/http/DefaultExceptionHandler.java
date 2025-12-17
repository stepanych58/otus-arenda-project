package ru.otus.msa.user.adapter.in.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.otus.msa.user.application.exception.InvalidXUserIdException;
import ru.otus.msa.user.application.exception.UserExistException;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(InvalidXUserIdException.class)
    public ResponseEntity<ErrorResponse> accessDenied(InvalidXUserIdException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse
                        .builder(
                                ex,
                                ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage()))
                        .build());
    }

    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<ErrorResponse> accessDenied(UserExistException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse
                        .builder(
                                ex,
                                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage()))
                        .build());
    }
}
