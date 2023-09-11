package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestDtoOut {
    private long id;
    private String description;
    private LocalDateTime created;
}
