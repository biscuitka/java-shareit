package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;

/**
 * Исходящий ДТО бронирования
 */
@Data
public class BookingDtoOut {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoShort item;
    private UserDtoShort booker;
    private StatusOfBooking status;


}
