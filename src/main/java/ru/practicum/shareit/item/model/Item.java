package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private User owner;
    private Boolean available;
    private ItemRequest request;
}
