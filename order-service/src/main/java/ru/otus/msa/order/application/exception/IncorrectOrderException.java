package ru.otus.msa.order.application.exception;

public class IncorrectOrderException extends RuntimeException {

    public IncorrectOrderException() {
        super("Невозможно создать заказ");
    }

    public IncorrectOrderException(String errorMessage) {
        super(errorMessage);
    }
}
