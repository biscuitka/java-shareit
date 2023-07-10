package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> itemStorage = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public Item createItem(Item item) {
        item.setId(idGenerator++);
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        long itemId = item.getId();
        if (!itemStorage.containsKey(itemId)) {
            throw new NotFoundException("Вещь не найдена, id " + itemId);
        }
        Item itemForUpdate = itemStorage.get(itemId);

        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        itemStorage.put(itemId, itemForUpdate);
        return itemForUpdate;
    }

    @Override
    public Item getById(long itemId) {
        return itemStorage.get(itemId);
    }

    @Override
    public List<Item> getBySearch(String search) {
        if (search.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> result = new ArrayList<>();

        for (Item item : itemStorage.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(search.toLowerCase())
                    || item.getDescription().toLowerCase().contains(search.toLowerCase()))) {
                result.add(item);
            }
        }

        return result;
    }

    @Override
    public List<Item> getAllByOwner(long ownerId) {
        return itemStorage.values()
                .stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long itemId) {
        if (itemId != 0 && itemStorage.containsKey(itemId)) {
            itemStorage.remove(itemId);
        } else {
            throw new NotFoundException("Вещь не найдена, id " + itemId);
        }
    }
}
