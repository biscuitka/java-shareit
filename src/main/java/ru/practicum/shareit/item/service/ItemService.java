package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getById(long itemId);

    List<ItemDto> getBySearch(String search);

    List<ItemDto> getAllByOwner(long ownerId);

    void deleteById(long itemId);

}
