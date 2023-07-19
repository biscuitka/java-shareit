package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "Поле name не может быть пустым")
    @Size(max = 40, message = "Имя должно быть кратким")
    private String name;
    @NotBlank(message = "Поле description не может быть пустым")
    private String description;
    @NotNull(message = "Доступность аренды должна быть указана")
    private Boolean available;
}
