package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long userId, LocalDateTime start, LocalDateTime end, Pageable pageable
    );

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, StatusOfBooking status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long userId, LocalDateTime start, LocalDateTime end, Pageable pageable
    );

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long userId, StatusOfBooking status, Pageable pageable);

    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            long itemId, StatusOfBooking status, LocalDateTime start
    );

    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime start);

    List<Booking> findAllByItemIdAndBookerIdAndEndBeforeAndStatus(
            long itemId, long userId, LocalDateTime start, StatusOfBooking status
    );

    List<Booking> findByItemIdAndStatus(long itemId, StatusOfBooking status);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);
}


