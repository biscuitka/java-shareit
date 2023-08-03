package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

@Getter
@Setter
@NoArgsConstructor
public class ItemDtoShort {
    private Long id;
    private String name;

    public ItemDtoShort(Item item) {
        setId(item.getId());
        setName(item.getName());
    }
}