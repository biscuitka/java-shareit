package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

public class EntityValidator {
    public static Booking getValidatedBooking(BookingRepository bookingRepository, long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено", HttpStatus.NOT_FOUND));
    }

    public static Item getValidatedItem(ItemRepository itemRepository, long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена", HttpStatus.NOT_FOUND));
    }

    public static ItemRequest getValidatedRequest(ItemRequestRepository requestRepository, long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден", HttpStatus.NOT_FOUND));
    }

    public static User getValidatedUser(UserRepository userRepository, long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }
}
