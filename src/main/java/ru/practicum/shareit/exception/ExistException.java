package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class ExistException extends RuntimeException {
    private HttpStatus httpStatus;

    public ExistException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}