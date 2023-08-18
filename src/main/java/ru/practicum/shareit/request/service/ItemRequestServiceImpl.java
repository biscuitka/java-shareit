package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public RequestDtoOut createRequest(long userId, RequestDtoIn itemRequestDtoIn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND));
        ItemRequest request = RequestMapper.fromDtoInToRequest(itemRequestDtoIn);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = requestRepository.save(request);
        return RequestMapper.fromRequestToDtoOut(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDtoWithItemsOut getById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден", HttpStatus.NOT_FOUND));
        List<Item> items = itemRepository.findAllByItemRequestId(requestId);
        return RequestMapper.fromRequestToDtoWithItemsOut(itemRequest, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoWithItemsOut> getAllByOwner(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND));
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId, pageable);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByItemRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getItemRequest().getId(),
                        Collectors.toList()
                ));
        return requests.stream()
                .map(request -> RequestMapper.fromRequestToDtoWithItemsOut(
                        request, itemsByRequestId.getOrDefault(request.getId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoWithItemsOut> getAll(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND));
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageable);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByItemRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getItemRequest().getId(),
                        Collectors.toList()
                ));

        return requests.stream()
                .map(request -> RequestMapper.fromRequestToDtoWithItemsOut(
                        request, itemsByRequestId.getOrDefault(request.getId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }
}

