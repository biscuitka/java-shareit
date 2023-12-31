package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.StateOfBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.HeaderConstants;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut createBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                       @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Создание нового бронирования: {}", bookingDtoIn);
        return bookingService.createBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut updateStatus(@PathVariable long bookingId,
                                      @RequestParam boolean approved,
                                      @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.info("Изменение статуса бронирования");
        return bookingService.updateStatus(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                    @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> getAllBookingByUser(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_STATE_VALUE) StateOfBooking state,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) int from,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.getAllBookingByUser(userId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllBookingsByItemOwner(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_STATE_VALUE) StateOfBooking state,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) int from,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.getAllBookingsByItemOwner(userId, state, pageable);
    }

}
