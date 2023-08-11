package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.StateOfBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.HeaderConstants;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut createBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                       @Valid @RequestBody BookingDtoIn bookingDtoIn) {
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
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        return bookingService.getAllBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllBookingsByItemOwner(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_STATE_VALUE) StateOfBooking state,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        return bookingService.getAllBookingsByItemOwner(userId, state, from, size);
    }

}
