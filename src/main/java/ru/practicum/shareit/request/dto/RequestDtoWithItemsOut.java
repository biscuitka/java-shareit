package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoRequested;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestDtoWithItemsOut {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoRequested> items;
}
