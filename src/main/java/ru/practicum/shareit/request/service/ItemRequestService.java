package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;

import java.util.List;

public interface ItemRequestService {
    RequestDtoOut createRequest(long userId, RequestDtoIn itemRequestDtoIn);

    RequestDtoWithItemsOut getById(long userId, long requestId);

    List<RequestDtoWithItemsOut> getAllByOwner(long userId, int from, int size);

    List<RequestDtoWithItemsOut> getAll(long userId, int from, int size);
}
