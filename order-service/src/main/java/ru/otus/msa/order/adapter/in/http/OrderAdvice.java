package ru.otus.msa.order.adapter.in.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.otus.msa.order.application.exception.IncorrectOrderException;

@ControllerAdvice
public class OrderAdvice {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ResponseErrorDto> handleException(Exception ex) {
        if (ex instanceof IncorrectOrderException || ex instanceof MissingRequestValueException) {
            return new ResponseEntity<>(new ResponseErrorDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(new ResponseErrorDto(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
