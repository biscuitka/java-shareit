package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.request.dto.RequestDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                @Valid @RequestBody RequestDtoIn itemRequestDtoIn) {
        log.info("Создание запроса" + itemRequestDtoIn);
        return requestClient.createRequest(userId, itemRequestDtoIn);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                          @PathVariable long requestId) {
        return requestClient.getById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
                                                @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        return requestClient.getAllByOwner(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                 @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
                                                 @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        return requestClient.getAllRequests(userId, from, size);
    }
}
