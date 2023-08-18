package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemDtoRequested {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
