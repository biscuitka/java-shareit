package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IncorrectException;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = User.getValidatedUser(userRepository, userId);
        Item item = ItemMapper.fromDtoToItem(itemDto);
        item.setOwner(owner);
        Item createdItem = itemRepository.save(item);

        return ItemMapper.fromItemToDto(createdItem);
    }

    @Override
    @Transactional
    public CommentDto createComment(long userId, CommentDto commentDto, long itemId) {
        User user = User.getValidatedUser(userRepository, userId);
        Item item = Item.getValidatedItem(itemRepository, itemId);
        Comment comment = CommentMapper.fromDtoToComment(commentDto);

        if (bookingRepository.findBookingsForCommentItem(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new IncorrectException("Нельзя оставить отзыв на вещь которую не бронировали");
        }
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.fromCommentToDto(savedComment);
    }


    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item oldItem = Item.getValidatedItem(itemRepository, itemId);
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
        Item item = Item.getValidatedItem(itemRepository, itemId);
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
        return items.stream().map(ItemMapper::fromItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllByOwner(long ownerId) {
        List<Item> items = itemRepository.findALLByOwnerIdOrderByIdAsc(ownerId);
        return items.stream().map(ItemMapper::fromItemToDto)
                .peek(this::setLastBooking)
                .peek(this::setNextBooking)
                .peek(this::setComments)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(long itemId) {
        itemRepository.deleteById(itemId);
    }

    private void setNextBooking(ItemDto itemDto) {
        Booking nextBooking = bookingRepository.findNextByItemId(itemDto.getId(),
                LocalDateTime.now());
        if (nextBooking != null) {
            itemDto.setNextBooking(new BookingDtoShort(nextBooking));
        }
    }

    private void setLastBooking(ItemDto itemDto) {
        Booking lastBooking = bookingRepository.findLastByItemId(itemDto.getId(),
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
}

