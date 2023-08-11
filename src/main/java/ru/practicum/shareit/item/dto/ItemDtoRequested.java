package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

@Getter
@Setter
@NoArgsConstructor
public class ItemDtoRequested {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

    public ItemDtoRequested(Item item) {
        setId(item.getId());
        setName(item.getName());
        setDescription(item.getDescription());
        setAvailable(item.getAvailable());
        setRequestId(item.getItemRequest().getId());
    }

}
