package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    @Query("select b from Booking b where b.booker.id = ?1 and ?2 between b.start and b.end order by b.start desc")
    List<Booking> findAllCurrentByUserId(long userId, LocalDateTime currentDateTime);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < current_timestamp order by b.start desc")
    List<Booking> findAllPastByUserId(long userId, LocalDateTime currentDateTime);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > current_timestamp order by b.start desc")
    List<Booking> findAllFutureByUserId(long userId, LocalDateTime currentDateTime);

    @Query("select b from Booking b where b.booker.id = ?1 AND b.status = ?2 order by b.start desc")
    List<Booking> findAllByBookerIdAndStatus(long userId, StatusOfBooking status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and ?2 between b.start and b.end order by b.start asc")
    List<Booking> findAllCurrentByOwnerId(long userId, LocalDateTime currentDateTime);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < current_timestamp order by b.start desc")
    List<Booking> findAllPastByOwnerId(long userId, LocalDateTime currentDateTime);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > current_timestamp order by b.start desc")
    List<Booking> findAllFByOwnerId(long userId, LocalDateTime currentDateTime);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findAllByItemOwnerIdAndStatus(long userId, StatusOfBooking status);

    @Query(value = "select * from bookings b where b.item_id = :itemId and b.status = 'APPROVED' " +
            "and b.start_date > :now order by b.start_date asc limit 1;", nativeQuery = true)
    Booking findNextByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query(value = "select * from bookings b where b.item_id = :itemId AND b.start_date < :now " +
            "order by b.start_date desc limit 1;", nativeQuery = true)
    Booking findLastByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query(value = "select * from bookings b where b.item_id = :itemId and b.booker_id = :userId " +
            "and b.end_date < :now and b.status = 'APPROVED'", nativeQuery = true)
    List<Booking> findBookingsForCommentItem(@Param("itemId") Long itemId, @Param("userId") Long userId,
                                             @Param("now") LocalDateTime now);

}


