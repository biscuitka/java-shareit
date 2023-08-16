package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.StateOfBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.DataTest;
import ru.practicum.shareit.util.TestConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void createBookingTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking booking = DataTest.testFutureBooking1();
        booking.setBooker(booker);
        booking.setItem(item);

        BookingDtoIn bookingDtoIn = DataTest.testBookingDtoIn2();
        bookingDtoIn.setItemId(item.getId());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED)))
                .thenReturn(List.of(DataTest.testLastBooking1()));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOut bookingDtoOut = bookingService.createBooking(booker.getId(), bookingDtoIn);

        assertThat(bookingDtoOut.getId(), equalTo(booking.getId()));
        assertThat(bookingDtoOut.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingDtoOut.getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookingDtoOut.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingDtoOut.getItem().getName(), equalTo(item.getName()));

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createBookingNotAvailableThrowExceptionTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);
        item.setAvailable(false);

        BookingDtoIn bookingDtoIn = DataTest.testBookingDtoIn2();
        bookingDtoIn.setItemId(item.getId());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));

        BookingAvailableException exception = assertThrows(
                BookingAvailableException.class,
                () -> bookingService.createBooking(booker.getId(), bookingDtoIn),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Вещь недоступна для бронирования", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createBookingByOwnerThrowExceptionTest() {
        User owner = DataTest.testUser1();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        BookingDtoIn bookingDtoIn = DataTest.testBookingDtoIn2();
        bookingDtoIn.setItemId(item.getId());

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(owner.getId(), bookingDtoIn),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("Владелец не может бронировать свою вещь", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    /**
     * Новое бронирование не может быть создано во временных рамках существующего
     */
    @Test
    void createBookingInCrossedDatesThrowExceptionTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking crossedBookingInDate = DataTest.testCrossWithBookingDtoIn1();
        crossedBookingInDate.setItem(item);

        BookingDtoIn bookingDtoIn = DataTest.testBookingDtoIn();
        bookingDtoIn.setItemId(item.getId());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED)))
                .thenReturn(List.of(crossedBookingInDate));


        BookingAvailableException exception = assertThrows(
                BookingAvailableException.class,
                () -> bookingService.createBooking(booker.getId(), bookingDtoIn),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Вещь забронирована на запрашиваемые даты", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    /**
     * Бронь не должна заканчиваться во время существующего бронирования
     */
    @Test
    void createBookingInCrossedDatesStartBeforeThrowExceptionTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking crossedBookingStartBefore = DataTest.testCrossWithBookingDtoIn2();
        crossedBookingStartBefore.setItem(item);

        BookingDtoIn bookingDtoIn = DataTest.testBookingDtoIn();
        bookingDtoIn.setItemId(item.getId());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED)))
                .thenReturn(List.of(crossedBookingStartBefore));


        BookingAvailableException exception = assertThrows(
                BookingAvailableException.class,
                () -> bookingService.createBooking(booker.getId(), bookingDtoIn),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Вещь забронирована на запрашиваемые даты", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    /**
     * Начало нового бронирования не должно быть во время существующего
     */
    @Test
    void createBookingInCrossedDatesEndAfterThrowExceptionTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking crossedBookingEndAfter = DataTest.testCrossWithBookingDtoIn3();
        crossedBookingEndAfter.setItem(item);

        BookingDtoIn bookingDtoIn = DataTest.testBookingDtoIn();
        bookingDtoIn.setItemId(item.getId());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED)))
                .thenReturn(List.of(crossedBookingEndAfter));


        BookingAvailableException exception = assertThrows(
                BookingAvailableException.class,
                () -> bookingService.createBooking(booker.getId(), bookingDtoIn),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Вещь забронирована на запрашиваемые даты", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    /**
     * Начало нового бронирования не должно быть одновременно с окончанием существующего
     */
    @Test
    void createBookingInCrossedDatesStartInParallelEndThrowExceptionTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking crossedBookingInParallel = DataTest.testCrossWithBookingDtoIn4();
        crossedBookingInParallel.setItem(item);

        BookingDtoIn bookingDtoIn = DataTest.testBookingDtoIn();
        bookingDtoIn.setItemId(item.getId());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED)))
                .thenReturn(List.of(crossedBookingInParallel));


        BookingAvailableException exception = assertThrows(
                BookingAvailableException.class,
                () -> bookingService.createBooking(booker.getId(), bookingDtoIn),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Вещь забронирована на запрашиваемые даты", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    /**
     * Окончание нового бронирования не должно быть одновременно со стартом существующего
     */
    @Test
    void createBookingInCrossedDatesEndInParallelStartThrowExceptionTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking crossedBookingInParallel = DataTest.testCrossWithBookingDtoIn5();
        crossedBookingInParallel.setItem(item);

        BookingDtoIn bookingDtoIn = DataTest.testBookingDtoIn();
        bookingDtoIn.setItemId(item.getId());

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED)))
                .thenReturn(List.of(crossedBookingInParallel));


        BookingAvailableException exception = assertThrows(
                BookingAvailableException.class,
                () -> bookingService.createBooking(booker.getId(), bookingDtoIn),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Вещь забронирована на запрашиваемые даты", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findByItemIdAndStatus(eq(item.getId()), eq(StatusOfBooking.APPROVED));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }


    @Test
    void updateStatusTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking booking = DataTest.testFutureWaitingBooking3();
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOut bookingDtoOut = bookingService.updateStatus(owner.getId(), true, booking.getId());

        assertThat(bookingDtoOut.getId(), equalTo(booking.getId()));
        assertThat(bookingDtoOut.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingDtoOut.getStatus(), equalTo(StatusOfBooking.APPROVED));
        assertThat(bookingDtoOut.getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookingDtoOut.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingDtoOut.getItem().getName(), equalTo(item.getName()));

        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void updateStatusByNotOwnerThrowExceptionTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking booking = DataTest.testFutureWaitingBooking3();
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(booker.getId(), true, booking.getId()),
                "Должно быть выброшено исключение"
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("Только владелец может подтвердить/отклонить бронь", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void updateStatusAlreadyApprovedThrowExceptionTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking booking = DataTest.testFutureWaitingBooking3();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(StatusOfBooking.APPROVED);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));

        BookingAvailableException exception = assertThrows(
                BookingAvailableException.class,
                () -> bookingService.updateStatus(owner.getId(), true, booking.getId()),
                "Должно быть выброшено исключение"
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Бронь уже подтверждена или отклонена", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getBookingByOwnerTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking booking = DataTest.testFutureWaitingBooking3();
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));

        BookingDtoOut bookingDtoByOwnerOut = bookingService.getBooking(owner.getId(), booking.getId());

        assertThat(bookingDtoByOwnerOut.getId(), equalTo(booking.getId()));
        assertThat(bookingDtoByOwnerOut.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingDtoByOwnerOut.getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookingDtoByOwnerOut.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingDtoByOwnerOut.getItem().getName(), equalTo(item.getName()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);

    }

    @Test
    void getBookingByBookerTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking booking = DataTest.testFutureWaitingBooking3();
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));

        BookingDtoOut bookingDtoByBookerOut = bookingService.getBooking(owner.getId(), booking.getId());

        assertThat(bookingDtoByBookerOut.getId(), equalTo(booking.getId()));
        assertThat(bookingDtoByBookerOut.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingDtoByBookerOut.getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookingDtoByBookerOut.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingDtoByBookerOut.getItem().getName(), equalTo(item.getName()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getBookingByNotBookerAndNotOwnerThrowExceptionTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();
        User user = DataTest.testUser3();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking booking = DataTest.testFutureWaitingBooking3();
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.of(booking));
        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(user.getId(), booking.getId()),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("Для данного пользователя бронирования не найдены", exception.getMessage());

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getAllBookingByUserTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);
        Item item2 = DataTest.testItem2();
        item2.setOwner(owner);

        Booking lastBooking = DataTest.testLastBooking1();
        lastBooking.setBooker(booker);
        lastBooking.setItem(item2);

        Booking waitingBooking = DataTest.testFutureWaitingBooking3();
        waitingBooking.setBooker(booker);
        waitingBooking.setItem(item);

        Booking futureBooking = DataTest.testFutureBooking1();
        futureBooking.setBooker(booker);
        futureBooking.setItem(item2);

        List<Booking> bookings = List.of(waitingBooking, lastBooking, futureBooking);

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(
                eq(booker.getId()), any(Pageable.class))).thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                (eq(booker.getId())), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(lastBooking));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(futureBooking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                eq(booker.getId()), eq(StatusOfBooking.WAITING), any(Pageable.class))).thenReturn(List.of(waitingBooking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                eq(booker.getId()), eq(StatusOfBooking.REJECTED), any(Pageable.class))).thenReturn(new ArrayList<>());

        List<BookingDtoOut> bookingDtoOutAllList = bookingService
                .getAllBookingByUser(booker.getId(), StateOfBooking.ALL, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutCurrentList = bookingService
                .getAllBookingByUser(booker.getId(), StateOfBooking.CURRENT, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutPastList = bookingService
                .getAllBookingByUser(booker.getId(), StateOfBooking.PAST, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutFutureList = bookingService
                .getAllBookingByUser(booker.getId(), StateOfBooking.FUTURE, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutWaitingList = bookingService
                .getAllBookingByUser(booker.getId(), StateOfBooking.WAITING, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutRejectedList = bookingService
                .getAllBookingByUser(booker.getId(), StateOfBooking.REJECTED, TestConstants.PAGEABLE);

        assertThat(bookingDtoOutAllList.size(), equalTo(3));
        assertThat(bookingDtoOutCurrentList.size(), equalTo(0));
        assertThat(bookingDtoOutPastList.size(), equalTo(1));
        assertThat(bookingDtoOutPastList.get(0).getId(), equalTo(lastBooking.getId()));
        assertThat(bookingDtoOutFutureList.size(), equalTo(1));
        assertThat(bookingDtoOutFutureList.get(0).getId(), equalTo(futureBooking.getId()));
        assertThat(bookingDtoOutWaitingList.get(0).getId(), equalTo(waitingBooking.getId()));
        assertThat(bookingDtoOutRejectedList.size(), equalTo(0));

        verify(userRepository, times(6)).findById(eq(booker.getId()));
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(
                eq(booker.getId()), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                (eq(booker.getId())), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookerIdAndEndBeforeOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartAfterOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatusOrderByStartDesc(
                eq(booker.getId()), eq(StatusOfBooking.WAITING), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatusOrderByStartDesc(
                eq(booker.getId()), eq(StatusOfBooking.REJECTED), any(Pageable.class));

        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getAllBookingsByItemOwnerTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);
        Item item2 = DataTest.testItem2();
        item2.setOwner(owner);

        Booking lastBooking = DataTest.testLastBooking1();
        lastBooking.setBooker(booker);
        lastBooking.setItem(item2);

        Booking waitingBooking = DataTest.testFutureWaitingBooking3();
        waitingBooking.setBooker(booker);
        waitingBooking.setItem(item);

        Booking futureBooking = DataTest.testFutureBooking1();
        futureBooking.setBooker(booker);
        futureBooking.setItem(item2);

        List<Booking> bookings = List.of(waitingBooking, lastBooking, futureBooking);

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                eq(owner.getId()), any(Pageable.class))).thenReturn(bookings);
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                (eq(owner.getId())), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(lastBooking));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(futureBooking));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                eq(owner.getId()), eq(StatusOfBooking.WAITING), any(Pageable.class))).thenReturn(List.of(waitingBooking));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                eq(owner.getId()), eq(StatusOfBooking.REJECTED), any(Pageable.class))).thenReturn(new ArrayList<>());

        List<BookingDtoOut> bookingDtoOutAllList = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.ALL, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutCurrentList = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.CURRENT, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutPastList = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.PAST, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutFutureList = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.FUTURE, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutWaitingList = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.WAITING, TestConstants.PAGEABLE);
        List<BookingDtoOut> bookingDtoOutRejectedList = bookingService
                .getAllBookingsByItemOwner(owner.getId(), StateOfBooking.REJECTED, TestConstants.PAGEABLE);

        assertThat(bookingDtoOutAllList.size(), equalTo(3));
        assertThat(bookingDtoOutCurrentList.size(), equalTo(0));
        assertThat(bookingDtoOutPastList.size(), equalTo(1));
        assertThat(bookingDtoOutPastList.get(0).getId(), equalTo(lastBooking.getId()));
        assertThat(bookingDtoOutFutureList.size(), equalTo(1));
        assertThat(bookingDtoOutFutureList.get(0).getId(), equalTo(futureBooking.getId()));
        assertThat(bookingDtoOutWaitingList.get(0).getId(), equalTo(waitingBooking.getId()));
        assertThat(bookingDtoOutRejectedList.size(), equalTo(0));

        verify(userRepository, times(6)).findById(eq(owner.getId()));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdOrderByStartDesc(
                eq(owner.getId()), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                (eq(owner.getId())), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDesc(
                eq(owner.getId()), eq(StatusOfBooking.WAITING), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDesc(
                eq(owner.getId()), eq(StatusOfBooking.REJECTED), any(Pageable.class));

        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }
}