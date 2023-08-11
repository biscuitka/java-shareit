package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class BookingAvailableException extends RuntimeException {
    private HttpStatus httpStatus;

    public BookingAvailableException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
