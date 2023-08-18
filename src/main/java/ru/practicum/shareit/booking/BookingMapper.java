package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking fromDtoInToBooking(BookingDtoIn bookingDtoIn) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoIn.getStart());
        booking.setEnd(bookingDtoIn.getEnd());
        return booking;
    }

    public static BookingDtoOut fromBookingToDtoOut(Booking booking) {
        BookingDtoOut dto = new BookingDtoOut();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(ItemMapper.fromItemToItemDtoShort(booking.getItem()));
        dto.setBooker(UserMapper.fromUserToUserDtoShort(booking.getBooker()));
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public static BookingDtoShort fromBookingToBookingDtoShort(Booking booking) {
        BookingDtoShort bookingDtoShort = new BookingDtoShort();
        bookingDtoShort.setId(booking.getId());
        bookingDtoShort.setBookerId(booking.getBooker().getId());
        return bookingDtoShort;
    }

    public static List<BookingDtoOut> fromListOfBookingToDtoOut(List<Booking> booking) {
        return booking.stream()
                .map(BookingMapper::fromBookingToDtoOut)
                .collect(Collectors.toList());
    }
}
