package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.StateOfBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingAvailableException;
import ru.practicum.shareit.exception.IncorrectException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDtoOut createBooking(long userId, BookingDtoIn bookingDtoIn) {
        User booker = User.getValidatedUser(userRepository, userId);
        Item item = Item.getValidatedItem(itemRepository, bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new BookingAvailableException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        Booking booking = BookingMapper.fromDtoInToBooking(bookingDtoIn);
        booking.setStatus(StatusOfBooking.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.fromBookingToDtoOut(savedBooking);
    }

    @Override
    @Transactional
    public BookingDtoOut updateStatus(long userId, boolean approved, long bookingId) {
        Booking booking = Booking.getValidatedBooking(bookingRepository, bookingId);
        User.getValidatedUser(userRepository, userId);

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Только владелец может подтвердить/отклонить бронь");
        }
        if (!booking.getStatus().equals(StatusOfBooking.WAITING)) {
            throw new BookingAvailableException("Бронь уже подтверждена или отклонена");
        }
        booking.setStatus(approved ? StatusOfBooking.APPROVED : StatusOfBooking.REJECTED);


        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.fromBookingToDtoOut(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOut getBooking(long userId, long bookingId) {
        Booking booking = Booking.getValidatedBooking(bookingRepository, bookingId);
        User.getValidatedUser(userRepository, userId);

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Для данного пользователя бронирования не найдены");
        }
        return BookingMapper.fromBookingToDtoOut(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllBookingByUser(long userId, StateOfBooking stateOfBooking) {
        User.getValidatedUser(userRepository, userId);
        LocalDateTime now = LocalDateTime.now();
        switch (stateOfBooking) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentByUserId(userId, now)
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllPastByUserId(userId, now)
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllFutureByUserId(userId, now)
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatus(userId, StatusOfBooking.WAITING)
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatus(userId, StatusOfBooking.REJECTED)
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            default:
                throw new IncorrectException("Статус не существует");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllBookingsByItemOwner(long userId, StateOfBooking stateOfBooking) {
        User.getValidatedUser(userRepository, userId);
        switch (stateOfBooking) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId)
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentByOwnerId(userId, LocalDateTime.now())
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllPastByOwnerId(userId, LocalDateTime.now())
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllFByOwnerId(userId, LocalDateTime.now())
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, StatusOfBooking.WAITING)
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, StatusOfBooking.REJECTED)
                        .stream().map(BookingMapper::fromBookingToDtoOut).collect(Collectors.toList());
            default:
                throw new IncorrectException("Статус не существует");
        }
    }
}
