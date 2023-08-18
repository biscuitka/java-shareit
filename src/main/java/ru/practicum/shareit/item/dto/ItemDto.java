package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "Поле name не может быть пустым")
    @Size(max = 100, message = "Имя должно быть кратким")
    private String name;
    @NotBlank(message = "Поле description не может быть пустым")
    private String description;
    @NotNull(message = "Доступность аренды должна быть указана")
    private Boolean available;

    private BookingDtoShort nextBooking;
    private BookingDtoShort lastBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
