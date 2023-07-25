package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;

@Getter
@Setter
@NoArgsConstructor
public class BookingDtoShort {
    private Long id;
    private Long bookerId;

    public BookingDtoShort(Booking booking) {
        setId(booking.getId());
        setBookerId(booking.getBooker().getId());
    }
}
