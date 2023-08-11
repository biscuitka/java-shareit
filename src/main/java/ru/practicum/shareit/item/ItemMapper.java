package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequested;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static Item fromDtoToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemDto fromItemToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId((item.getItemRequest() != null) ? item.getItemRequest().getId() : null);
        return dto;
    }

    public static ItemDtoRequested fromItemToItemDtoRequested(Item item) {
        ItemDtoRequested dto = new ItemDtoRequested();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getItemRequest().getId());
        return dto;
    }

    public static List<ItemDto> fromListOfItemToDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::fromItemToDto)
                .collect(Collectors.toList());
    }

    public static List<ItemDtoRequested> fromListOfItemToDtoRequested(List<Item> items) {
        return items.stream()
                .map(ItemMapper::fromItemToItemDtoRequested)
                .collect(Collectors.toList());
    }
}
