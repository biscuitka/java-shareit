package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Входящий ДТО бронирования
 */
@Data
public class BookingDtoIn {

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;


}
