package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    CommentDto createComment(long userId, CommentDto commentDto, long itemId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getById(long itemId, long userId);

    List<ItemDto> getBySearch(String search, int from, int size);

    List<ItemDto> getAllByOwner(long ownerId, int from, int size);

    void deleteById(long itemId);

}
