package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IncorrectException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = UserServiceImpl.getValidatedUser(userRepository, userId);
        Item item = ItemMapper.fromDtoToItem(itemDto);
        item.setOwner(owner);
        Item createdItem = itemRepository.save(item);

        return ItemMapper.fromItemToDto(createdItem);
    }

    @Override
    public CommentDto createComment(long userId, CommentDto commentDto, long itemId) {
        User user = UserServiceImpl.getValidatedUser(userRepository, userId);
        Item item = getValidatedItem(itemRepository, itemId);
        Comment comment = CommentMapper.fromDtoToComment(commentDto);

        if (bookingRepository.findAllByItemIdAndBookerIdAndEndBeforeAndStatus(itemId, userId, LocalDateTime.now(), StatusOfBooking.APPROVED).isEmpty()) {
            throw new IncorrectException("Нельзя оставить отзыв на вещь которую не бронировали");
        }
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.fromCommentToDto(savedComment);
    }


    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item oldItem = getValidatedItem(itemRepository, itemId);
        if (oldItem.getOwner().getId() != userId) {
            throw new AccessDeniedException("Пользователь не является владельцем объекта");
        }
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(oldItem);
        return ItemMapper.fromItemToDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(long itemId, long userId) {
        Item item = getValidatedItem(itemRepository, itemId);
        ItemDto itemDto = ItemMapper.fromItemToDto(item);
        setComments(itemDto);
        if (item.getOwner().getId() == userId) {
            setNextBooking(itemDto);
            setLastBooking(itemDto);
        }
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getBySearch(String search) {
        if (search.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findAllBySearch(search);
        return items.stream()
                .map(ItemMapper::fromItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllByOwner(long ownerId) {
        List<Item> items = itemRepository.findALLByOwnerIdOrderByIdAsc(ownerId);
        List<ItemDto> itemDtos = ItemMapper.fromListOfItemToDto(items);
        setBookingsForList(itemDtos);
        setCommentsForListItems(itemDtos);
        return itemDtos;
    }

    @Override
    public void deleteById(long itemId) {
        itemRepository.deleteById(itemId);
    }

    private void setNextBooking(ItemDto itemDto) {
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(),
                StatusOfBooking.APPROVED, LocalDateTime.now());
        if (nextBooking != null) {
            itemDto.setNextBooking(new BookingDtoShort(nextBooking));
        }
    }

    private void setLastBooking(ItemDto itemDto) {
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(itemDto.getId(),
                LocalDateTime.now());
        if (lastBooking != null) {
            itemDto.setLastBooking(new BookingDtoShort(lastBooking));
        }
    }

    private void setComments(ItemDto itemDto) {
        itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream()
                .map(CommentMapper::fromCommentToDto)
                .collect(Collectors.toList()));

    }

    private void setCommentsForListItems(List<ItemDto> list) {
        List<Comment> comments = commentRepository.findAll();
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::fromCommentToDto)
                .collect(Collectors.toList());
        list.forEach(itemDto -> {
            List<CommentDto> itemComments = commentDtos.stream()
                    .filter(commentDto -> itemDto.getComments().contains(commentDto))
                    .collect(Collectors.toList());
            itemDto.setComments(itemComments);
        });
    }

    private void setBookingsForList(List<ItemDto> list) {
        List<Long> itemIds = list.stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());

        Map<Long, List<Booking>> bookingsByItemId = bookingRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        list.parallelStream().forEach(dto -> {
            List<Booking> bookings = bookingsByItemId.get(dto.getId());
            if (bookings != null && !bookings.isEmpty()) {
                bookings.sort(Comparator.comparing(Booking::getStart));
                Booking nextBooking = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .filter(booking -> booking.getStatus().equals(StatusOfBooking.APPROVED))
                        .findFirst()
                        .orElse(null);
                Booking lastBooking = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .max(Comparator.comparing(Booking::getEnd))
                        .orElse(null);
                dto.setNextBooking(nextBooking != null ? new BookingDtoShort(nextBooking) : null);
                dto.setLastBooking(lastBooking != null ? new BookingDtoShort(lastBooking) : null);
            }
        });
    }

    public static Item getValidatedItem(ItemRepository itemRepository, long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }
}

