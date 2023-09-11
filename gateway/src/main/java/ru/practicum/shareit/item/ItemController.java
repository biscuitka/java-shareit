package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Создание вещи: {}", itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @PathVariable long itemId) {
        return itemClient.createComment(userId, commentDto, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId) {
        log.info("Обновление вещи: {}", itemDto);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                          @PathVariable long itemId) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
                                                @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        return itemClient.getAllByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getBySearch(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                              @RequestParam(value = "text") String search,
                                              @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
                                              @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        return itemClient.getBySearch(userId, search, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@PathVariable long itemId) {
        itemClient.deleteById(itemId);
    }


}
