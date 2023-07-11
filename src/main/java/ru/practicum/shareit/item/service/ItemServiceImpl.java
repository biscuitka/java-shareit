package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = userDao.getUserById(userId);
        Item item = itemMapper.fromDtoToItem(itemDto);
        Item createdItem = itemDao.createItem(item);
        item.setOwner(owner);
        return itemMapper.fromItemToDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item oldItem = itemDao.getById(itemId);
        if (oldItem.getOwner().getId() != userId) {
            throw new AccessDeniedException("Пользователь не является владельцем объекта");
        }
        Item itemToUpdate = itemMapper.fromDtoToItem(itemDto);
        itemToUpdate.setId(itemId);
        Item updatedItem = itemDao.updateItem(itemToUpdate);
        return itemMapper.fromItemToDto(updatedItem);
    }

    @Override
    public ItemDto getById(long itemId) {
        return itemMapper.fromItemToDto(itemDao.getById(itemId));
    }

    @Override
    public List<ItemDto> getBySearch(String search) {
        return itemDao.getBySearch(search)
                .stream().map(itemMapper::fromItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByOwner(long ownerId) {
        return itemDao.getAllByOwner(ownerId)
                .stream().map(itemMapper::fromItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long itemId) {
        itemDao.deleteById(itemId);
    }
}
