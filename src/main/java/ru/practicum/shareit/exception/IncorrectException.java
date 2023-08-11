package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class IncorrectException extends RuntimeException {
    private HttpStatus httpStatus;

    public IncorrectException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
