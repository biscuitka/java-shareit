package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item createItem(Item item);

    Item updateItem(Item item);

    Item getById(long itemId);

    List<Item> getBySearch(String search);

    List<Item> getAllByOwner(long ownerId);

    void deleteById(long itemId);

}