package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
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
        User user = UserServiceImpl.getValidatedUser(userRepository, userId);
        ItemRequest request = RequestMapper.fromDtoInToRequest(itemRequestDtoIn);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = requestRepository.save(request);
        return RequestMapper.fromRequestToDtoOut(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDtoWithItemsOut getById(long userId, long requestId) {
        UserServiceImpl.getValidatedUser(userRepository, userId);
        ItemRequest itemRequest = getValidatedRequest(requestRepository, requestId);
        List<Item> items = itemRepository.findAllByItemRequestId(requestId);
        return RequestMapper.fromRequestToDtoWithItemsOut(itemRequest, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoWithItemsOut> getAllByOwner(long userId, int from, int size) {
        UserServiceImpl.getValidatedUser(userRepository, userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId, pageable);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByItemRequestIdIn(requestIds);
        return requests.stream()
                .map((ItemRequest request) -> RequestMapper.fromRequestToDtoWithItemsOut(request, items))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoWithItemsOut> getAll(long userId, int from, int size) {
        UserServiceImpl.getValidatedUser(userRepository, userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageable);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByItemRequestIdIn(requestIds);
        return requests.stream()
                .map((ItemRequest request) -> RequestMapper.fromRequestToDtoWithItemsOut(request, items))
                .collect(Collectors.toList());
    }

    public static ItemRequest getValidatedRequest(ItemRequestRepository requestRepository, long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден", HttpStatus.NOT_FOUND));
    }
}
