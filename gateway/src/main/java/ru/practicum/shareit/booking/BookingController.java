package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.StateOfBooking;
import ru.practicum.shareit.constants.HeaderConstants;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Создание нового бронирования: {}", bookingDtoIn);
        return bookingClient.createBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@PathVariable long bookingId,
                                               @RequestParam boolean approved,
                                               @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.info("Изменение статуса бронирования");
        return bookingClient.updateStatus(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                             @PathVariable long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingByUser(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_STATE_VALUE) @Valid StateOfBooking state,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        return bookingClient.getAllBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByItemOwner(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_STATE_VALUE) @Valid StateOfBooking state,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
            @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        return bookingClient.getAllBookingsByItemOwner(userId, state, from, size);
    }

}
