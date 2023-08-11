package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequested;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    public static ItemRequest fromDtoInToRequest(RequestDtoIn requestDtoIn) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDtoIn.getDescription());
        return request;
    }

    public static RequestDtoOut fromRequestToDtoOut(ItemRequest request) {
        RequestDtoOut dto = new RequestDtoOut();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public static RequestDtoWithItemsOut fromRequestToDtoWithItemsOut(ItemRequest request, List<Item> items) {
        RequestDtoWithItemsOut dto = new RequestDtoWithItemsOut();
        List<ItemDtoRequested> requestedItems = ItemMapper.fromListOfItemToDtoRequested(items);
        List<ItemDtoRequested> resultList = new ArrayList<>();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        for (ItemDtoRequested item : requestedItems) {
            if (item.getRequestId() == dto.getId()) {
                resultList.add(item);
            }
        }
        dto.setItems(resultList);
        return dto;
    }
}
