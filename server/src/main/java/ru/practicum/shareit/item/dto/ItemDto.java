package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private BookingDtoShort nextBooking;
    private BookingDtoShort lastBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
