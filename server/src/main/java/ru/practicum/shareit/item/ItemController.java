package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Создание вещи: {}", itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable long itemId) {
        return itemService.createComment(userId, commentDto, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        log.info("Обновление вещи: {}", itemDto);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                           @PathVariable long itemId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                       @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) int from,
                                       @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemService.getAllByOwner(userId, pageable);
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearch(@RequestParam(value = "text") String search,
                                     @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) int from,
                                     @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemService.getBySearch(search, pageable);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@PathVariable long itemId) {
        itemService.deleteById(itemId);
    }


}
