package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.StateOfBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.DataIntegrationTest;
import ru.practicum.shareit.util.TestConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceImplIntegrationTest {
    @Autowired
    BookingService bookingService;

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void getAllBookingByUser() {
        User owner = DataIntegrationTest.testUser1();
        userRepository.save(owner);
        User booker = DataIntegrationTest.testUser2();
        userRepository.save(booker);
        Item item = DataIntegrationTest.testItem1();
        item.setOwner(owner);
        Item item2 = DataIntegrationTest.testItem3();
        item2.setOwner(owner);
        itemRepository.saveAll(List.of(item, item2));
        Booking booking = DataIntegrationTest.testLastBooking1();
        booking.setItem(item);
        booking.setBooker(booker);
        Booking booking2 = DataIntegrationTest.testLastBooking2();
        booking2.setItem(item2);
        booking2.setBooker(booker);
        Booking booking3 = DataIntegrationTest.testFutureBooking1();
        booking3.setItem(item2);
        booking3.setBooker(booker);
        bookingRepository.saveAll(List.of(booking, booking2, booking3));

        List<BookingDtoOut> bookingDtoOutList = bookingService
                .getAllBookingByUser(booker.getId(), StateOfBooking.ALL, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutList2 = bookingService
                .getAllBookingByUser(booker.getId(), StateOfBooking.FUTURE, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutList3 = bookingService
                .getAllBookingByUser(booker.getId(), StateOfBooking.PAST, TestConstants.PAGEABLE);

        assertThat(bookingDtoOutList).hasSize(3);
        assertEquals("Доспех", bookingDtoOutList.get(0).getItem().getName());
        assertEquals("Штаны", bookingDtoOutList.get(1).getItem().getName());
        assertEquals("Доспех", bookingDtoOutList.get(2).getItem().getName());

        assertThat(bookingDtoOutList2).hasSize(1);
        assertEquals("Доспех", bookingDtoOutList.get(0).getItem().getName());
        assertEquals(booker.getId(), bookingDtoOutList.get(0).getBooker().getId());

        assertThat(bookingDtoOutList3).hasSize(2);
        assertEquals("Доспех", bookingDtoOutList.get(0).getItem().getName());
        assertEquals(booker.getId(), bookingDtoOutList.get(0).getBooker().getId());
        assertEquals("Штаны", bookingDtoOutList.get(1).getItem().getName());
    }

    @Test
    void getAllBookingsByItemOwner() {
        User owner = DataIntegrationTest.testUser1();
        userRepository.save(owner);
        User booker = DataIntegrationTest.testUser2();
        userRepository.save(booker);
        Item item = DataIntegrationTest.testItem1();
        item.setOwner(owner);
        Item item2 = DataIntegrationTest.testItem3();
        item2.setOwner(owner);
        itemRepository.saveAll(List.of(item, item2));
        Booking booking = DataIntegrationTest.testLastBooking1();
        booking.setItem(item);
        booking.setBooker(booker);
        Booking booking2 = DataIntegrationTest.testLastBooking2();
        booking2.setItem(item2);
        booking2.setBooker(booker);
        Booking booking3 = DataIntegrationTest.testFutureBooking1();
        booking3.setItem(item2);
        booking3.setBooker(booker);
        booking3.setStatus(StatusOfBooking.WAITING);
        bookingRepository.saveAll(List.of(booking, booking2, booking3));

        List<BookingDtoOut> bookingDtoOutList = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.ALL, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutList2 = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.FUTURE, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutList3 = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.PAST, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutList4 = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.WAITING, TestConstants.PAGEABLE);

        assertThat(bookingDtoOutList).hasSize(3);
        assertThat(bookingDtoOutList2).hasSize(1);
        assertThat(bookingDtoOutList3).hasSize(2);
        assertThat(bookingDtoOutList4).hasSize(1);

    }
}