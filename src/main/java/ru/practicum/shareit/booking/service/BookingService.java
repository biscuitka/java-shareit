package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.StateOfBooking;

import java.util.List;

public interface BookingService {

    BookingDtoOut createBooking(long userId, BookingDtoIn bookingDtoIn);

    BookingDtoOut updateStatus(long userId, boolean approved, long bookingId);

    BookingDtoOut getBooking(long userId, long bookingId);

    List<BookingDtoOut> getAllBookingByUser(long userId, StateOfBooking stateOfBooking, int from, int size);

    List<BookingDtoOut> getAllBookingsByItemOwner(long userId, StateOfBooking stateOfBooking, int from, int size);
}