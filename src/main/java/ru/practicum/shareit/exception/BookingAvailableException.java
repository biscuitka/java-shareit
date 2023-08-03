package ru.practicum.shareit.exception;

public class BookingAvailableException extends RuntimeException {
    public BookingAvailableException(String message) {
        super(message);
    }
}
