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
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDtoOut createBooking(long userId, BookingDtoIn bookingDtoIn) {
        User booker = UserServiceImpl.getValidatedUser(userRepository, userId);
        Item item = ItemServiceImpl.getValidatedItem(itemRepository, bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new BookingAvailableException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }
        List<Booking> bookings = bookingRepository.findByItemIdAndStatus(item.getId(), StatusOfBooking.APPROVED);
        boolean isConflicting = !bookings.stream().allMatch(booking ->
                !booking.getStart().isBefore(bookingDtoIn.getEnd()) || !booking.getEnd().isAfter(bookingDtoIn.getEnd())
        );
        if (isConflicting) {
            throw new BookingAvailableException("Вещь забронирована на запрашиваемые даты");
        }

        Booking booking = BookingMapper.fromDtoInToBooking(bookingDtoIn);
        booking.setStatus(StatusOfBooking.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.fromBookingToDtoOut(savedBooking);
    }

    @Override
    public BookingDtoOut updateStatus(long userId, boolean approved, long bookingId) {
        Booking booking = getValidatedBooking(bookingRepository, bookingId);
        UserServiceImpl.getValidatedUser(userRepository, userId);

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
        Booking booking = getValidatedBooking(bookingRepository, bookingId);
        UserServiceImpl.getValidatedUser(userRepository, userId);

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Для данного пользователя бронирования не найдены");
        }
        return BookingMapper.fromBookingToDtoOut(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllBookingByUser(long userId, StateOfBooking stateOfBooking) {
        UserServiceImpl.getValidatedUser(userRepository, userId);
        LocalDateTime now = LocalDateTime.now();
        switch (stateOfBooking) {
            case ALL:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdOrderByStartDesc(userId));
            case CURRENT:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now));
            case PAST:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now));
            case FUTURE:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now));
            case WAITING:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.WAITING));
            case REJECTED:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.REJECTED));
            default:
                throw new IncorrectException("Статус не существует");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllBookingsByItemOwner(long userId, StateOfBooking stateOfBooking) {
        UserServiceImpl.getValidatedUser(userRepository, userId);
        LocalDateTime now = LocalDateTime.now();
        switch (stateOfBooking) {
            case ALL:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdOrderByStartDesc(userId));
            case CURRENT:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now));
            case PAST:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now));
            case FUTURE:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now));
            case WAITING:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.WAITING));
            case REJECTED:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.REJECTED));
            default:
                throw new IncorrectException("Статус не существует");
        }
    }

    public static Booking getValidatedBooking(BookingRepository bookingRepository, long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }
}
