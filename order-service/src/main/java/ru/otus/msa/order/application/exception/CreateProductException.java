package ru.otus.msa.order.application.exception;

public class CreateProductException extends RuntimeException {
    public CreateProductException() {
        super("Невозможно создать товар");
    }
}
