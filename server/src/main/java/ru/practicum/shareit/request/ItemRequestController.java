package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public RequestDtoOut createRequest(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                       @RequestBody RequestDtoIn itemRequestDtoIn) {
        log.info("Создание запроса" + itemRequestDtoIn);
        return requestService.createRequest(userId, itemRequestDtoIn);
    }

    @GetMapping("{requestId}")
    public RequestDtoWithItemsOut getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                          @PathVariable long requestId) {
        return requestService.getById(userId, requestId);
    }

    @GetMapping
    public List<RequestDtoWithItemsOut> getAllByOwner(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                      @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) int from,
                                                      @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return requestService.getAllByOwner(userId, pageable);
    }

    @GetMapping("/all")
    public List<RequestDtoWithItemsOut> getAllRequests(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                       @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) int from,
                                                       @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return requestService.getAll(userId, pageable);
    }
}
