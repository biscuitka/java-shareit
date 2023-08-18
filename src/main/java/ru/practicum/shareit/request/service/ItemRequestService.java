package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;

import java.util.List;

public interface ItemRequestService {
    RequestDtoOut createRequest(long userId, RequestDtoIn itemRequestDtoIn);

    RequestDtoWithItemsOut getById(long userId, long requestId);

    List<RequestDtoWithItemsOut> getAllByOwner(long userId, Pageable pageable);

    List<RequestDtoWithItemsOut> getAll(long userId, Pageable pageable);
}
