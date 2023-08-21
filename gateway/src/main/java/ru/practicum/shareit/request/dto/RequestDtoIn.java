package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RequestDtoIn {
    @NotBlank(message = "Поле description не может быть пустым")
    private String description;
}
