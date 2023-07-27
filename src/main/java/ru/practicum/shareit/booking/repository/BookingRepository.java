package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, StatusOfBooking status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long userId, StatusOfBooking status);

    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(long itemId, StatusOfBooking status, LocalDateTime start);

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime start);

    List<Booking> findAllByItemIdAndBookerIdAndEndBeforeAndStatus(long itemId, long userId, LocalDateTime start, StatusOfBooking status);

    List<Booking> findByItemIdAndStatus(long itemId, StatusOfBooking status);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);
}


