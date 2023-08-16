package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.util.EntityValidator;

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
        User booker = EntityValidator.getValidatedUser(userRepository, userId);
        Item item = EntityValidator.getValidatedItem(itemRepository, bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new BookingAvailableException("Вещь недоступна для бронирования", HttpStatus.BAD_REQUEST);
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец не может бронировать свою вещь", HttpStatus.NOT_FOUND);
        }
        List<Booking> bookings = bookingRepository.findByItemIdAndStatus(item.getId(), StatusOfBooking.APPROVED);

        boolean isConflicting = bookings.stream().anyMatch(booking -> isBookingConflict(booking, bookingDtoIn));
        if (isConflicting) {
            throw new BookingAvailableException("Вещь забронирована на запрашиваемые даты", HttpStatus.BAD_REQUEST);
        }

        Booking booking = BookingMapper.fromDtoInToBooking(bookingDtoIn);
        booking.setStatus(StatusOfBooking.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.fromBookingToDtoOut(savedBooking);
    }

    /**
     * newStartInsideExisting - начало нового бронирования находится внутри существующего бронирования.
     * newEndInsideExisting - конец нового бронирования находится внутри существующего бронирования.
     * newBookingContainsExisting - новое бронирование пересекает существующее бронирование с обеих сторон (полностью содержит его).
     * newBookingTouchesExisting - новое бронирование только касается существующего бронирования.
     *
     * @param existingBooking существующая бронь
     * @param newBooking      новая бронь
     * @return результат проверки на возможные пересечения во времени
     */
    private boolean isBookingConflict(Booking existingBooking, BookingDtoIn newBooking) {
        boolean newStartInsideExisting = existingBooking.getStart().isBefore(newBooking.getStart())
                && existingBooking.getEnd().isAfter(newBooking.getStart());
        boolean newEndInsideExisting = existingBooking.getStart().isBefore(newBooking.getEnd())
                && existingBooking.getEnd().isAfter(newBooking.getEnd());
        boolean newBookingContainsExisting = newBooking.getStart().isBefore(existingBooking.getStart())
                && newBooking.getEnd().isAfter(existingBooking.getEnd());
        boolean newBookingTouchesExisting = existingBooking.getEnd().isEqual(newBooking.getStart())
                || newBooking.getEnd().isEqual(existingBooking.getStart());

        return newStartInsideExisting || newEndInsideExisting || newBookingContainsExisting || newBookingTouchesExisting;
    }

    @Override
    public BookingDtoOut updateStatus(long userId, boolean approved, long bookingId) {
        Booking booking = EntityValidator.getValidatedBooking(bookingRepository, bookingId);
        EntityValidator.getValidatedUser(userRepository, userId);

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Только владелец может подтвердить/отклонить бронь", HttpStatus.NOT_FOUND);
        }
        if (!booking.getStatus().equals(StatusOfBooking.WAITING)) {
            throw new BookingAvailableException("Бронь уже подтверждена или отклонена", HttpStatus.BAD_REQUEST);
        }
        booking.setStatus(approved ? StatusOfBooking.APPROVED : StatusOfBooking.REJECTED);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.fromBookingToDtoOut(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOut getBooking(long userId, long bookingId) {
        Booking booking = EntityValidator.getValidatedBooking(bookingRepository, bookingId);
        EntityValidator.getValidatedUser(userRepository, userId);

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Для данного пользователя бронирования не найдены", HttpStatus.NOT_FOUND);
        }
        return BookingMapper.fromBookingToDtoOut(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllBookingByUser(long userId, StateOfBooking stateOfBooking, Pageable pageable) {
        EntityValidator.getValidatedUser(userRepository, userId);
        LocalDateTime now = LocalDateTime.now();
        switch (stateOfBooking) {
            case ALL:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdOrderByStartDesc(userId, pageable));
            case CURRENT:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, pageable));
            case PAST:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable));
            case FUTURE:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable));
            case WAITING:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.WAITING, pageable));
            case REJECTED:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.REJECTED, pageable));
            default:
                throw new IncorrectException("Статус не существует", HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> getAllBookingsByItemOwner(long userId, StateOfBooking stateOfBooking, Pageable pageable) {
        EntityValidator.getValidatedUser(userRepository, userId);
        LocalDateTime now = LocalDateTime.now();
        switch (stateOfBooking) {
            case ALL:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdOrderByStartDesc(userId, pageable));
            case CURRENT:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, pageable));
            case PAST:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now, pageable));
            case FUTURE:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now, pageable));
            case WAITING:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.WAITING, pageable));
            case REJECTED:
                return BookingMapper.fromListOfBookingToDtoOut(bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.REJECTED, pageable));
            default:
                throw new IncorrectException("Статус не существует", HttpStatus.BAD_REQUEST);
        }
    }
}
