package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequested;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class DataTest {

    public static long userId = 153;
    public static long itemId = 146;
    public static LocalDateTime time = LocalDateTime.now().withNano(0);

    public static User testUser1() {
        User user = new User();
        user.setId(1L);
        user.setName("Ciri");
        user.setEmail("princessOfCintra@ya.ru");
        return user;
    }

    public static User testUser2() {
        User user = new User();
        user.setId(2L);
        user.setName("Triss");
        user.setEmail("merigold@ya.ru");
        return user;
    }

    public static User testUser3() {
        User user = new User();
        user.setId(3L);
        user.setName("Julian");
        user.setEmail("ranunculus@ya.ru");
        return user;
    }

    public static UserDto testUserDto1() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Geralt");
        userDto.setEmail("butcherOfBlaviken@ya.ru");
        return userDto;
    }

    public static UserDto testUserDto2() {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("Yen");
        userDto.setEmail("yenneferOfVengerberg@ya.ru");
        return userDto;
    }

    public static Item testItem1() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Меч");
        item.setDescription("Улучшенный стальной меч школы волка");
        item.setOwner(testUser1());
        item.setAvailable(true);
        return item;
    }

    public static Item testItem2() {
        Item item = new Item();
        item.setId(2L);
        item.setName("Штаны");
        item.setDescription("Отличные штаны школы волка");
        item.setOwner(testUser1());
        item.setAvailable(true);
        return item;
    }

    public static Item testItem3() {
        Item item = new Item();
        item.setName("Доспех");
        item.setDescription("Доспех охотников за колдуньями");
        item.setOwner(testUser1());
        item.setAvailable(true);
        return item;
    }

    public static Item testItem4() {
        Item item = new Item();
        item.setId(4L);
        item.setName("Доспех");
        item.setDescription("Гроссмейстерский доспех школы волка");
        item.setOwner(testUser1());
        item.setAvailable(true);
        return item;
    }

    public static Item testItem5() {
        Item item = new Item();
        item.setName("Штаны");
        item.setDescription("Махакамские штаны");
        item.setOwner(testUser1());
        item.setAvailable(true);
        return item;
    }

    public static ItemDto testItemDto1() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("меч Школы Грифона");
        item.setDescription("Улучшенный стальной меч Школы Грифона");
        item.setAvailable(true);
        return item;
    }

    public static ItemDto testItemDto2() {
        ItemDto item = new ItemDto();
        item.setId(2L);
        item.setName("меч Школы Кота");
        item.setDescription("Отличный стальной меч Школы Кота");
        item.setAvailable(true);
        return item;
    }

    public static ItemDtoRequested testItemDtoRequested() {
        ItemDtoRequested item = new ItemDtoRequested();
        item.setId(2L);
        item.setName("Доспех");
        item.setDescription("Славный доспех рыцарей Пылающей Розы");
        item.setAvailable(true);
        return item;
    }

    public static Comment testComment1() {
        Comment comment = new Comment();
        comment.setId(6L);
        comment.setText("Классные шмотки");
        return comment;
    }

    public static Comment testComment2() {
        Comment comment = new Comment();
        comment.setId(7L);
        comment.setText("Все супер, но мне не подошло");
        return comment;
    }

    public static CommentDto testCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(5L);
        commentDto.setText("Замечательные штаны, рекомендую");
        commentDto.setAuthorName("Весемир");
        commentDto.setCreated(time);
        return commentDto;
    }

    public static ItemRequest testItemRequest1() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Нужен стальной меч");
        return itemRequest;
    }

    public static ItemRequest testItemRequest2() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(2L);
        itemRequest.setDescription("Ищу прочные штаны для битвы с гулем");
        return itemRequest;
    }

    public static RequestDtoIn testRequestDtoIn() {
        RequestDtoIn requestDtoIn = new RequestDtoIn();
        requestDtoIn.setDescription("Нужен мастерский стальной меч Школы Грифона");
        return requestDtoIn;
    }

    public static RequestDtoOut testRequestDtoOut() {
        RequestDtoOut requestDtoOut = new RequestDtoOut();
        requestDtoOut.setId(1L);
        requestDtoOut.setDescription("Нужен мастерский стальной меч Школы Грифона");
        requestDtoOut.setCreated(time);
        return requestDtoOut;
    }


    public static RequestDtoWithItemsOut testRequestDtoWithItemsOut1() {
        RequestDtoWithItemsOut requestDtoOut = new RequestDtoWithItemsOut();
        requestDtoOut.setId(2L);
        requestDtoOut.setDescription("Ищу перчатки Школы Кота");
        requestDtoOut.setItems(null);
        requestDtoOut.setCreated(time);
        return requestDtoOut;
    }

    public static RequestDtoWithItemsOut testRequestDtoWithItemsOut2() {
        RequestDtoWithItemsOut requestDtoOut = new RequestDtoWithItemsOut();
        requestDtoOut.setId(3L);
        requestDtoOut.setDescription("Нужен доспех Школы Кота");
        requestDtoOut.setItems(null);
        requestDtoOut.setCreated(time);
        return requestDtoOut;
    }

    public static RequestDtoWithItemsOut testRequestDtoWithItemsOut3() {
        RequestDtoWithItemsOut requestDtoOut = new RequestDtoWithItemsOut();
        requestDtoOut.setId(3L);
        requestDtoOut.setDescription("Нужен добротный доспех");
        requestDtoOut.setCreated(time);
        return requestDtoOut;
    }

    public static Booking testLastBooking1() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.minusDays(2));
        booking.setEnd(time.minusDays(1));
        return booking;
    }

    public static Booking testLastBooking2() {
        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.minusDays(3));
        booking.setEnd(time.minusDays(2));
        return booking;
    }

    public static Booking testFutureBooking1() {
        Booking booking = new Booking();
        booking.setId(3L);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.plusDays(1));
        booking.setEnd(time.plusDays(2));
        return booking;
    }

    /**
     * @return бронь начинается и заканчивается во время уже имеющейся брони
     */
    public static Booking testCrossWithBookingDtoIn1() {
        Booking booking = new Booking();
        booking.setId(4L);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.plusHours(2));
        booking.setEnd(time.plusHours(4));
        return booking;
    }

    /**
     * @return бронь начинается раньше, но заканчивается во время уже имеющейся брони
     */
    public static Booking testCrossWithBookingDtoIn2() {
        Booking booking = new Booking();
        booking.setId(5L);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time);
        booking.setEnd(time.plusHours(3));
        return booking;
    }

    /**
     * @return бронь начинается во время уже имеющейся брони раньше, но заканчивается позже
     */
    public static Booking testCrossWithBookingDtoIn3() {
        Booking booking = new Booking();
        booking.setId(6L);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.plusHours(4));
        booking.setEnd(time.plusHours(6));
        return booking;
    }

    /**
     * @return бронь начинается одновременно с окончанием имеющейся брони
     */
    public static Booking testCrossWithBookingDtoIn4() {
        Booking booking = new Booking();
        booking.setId(7L);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time);
        booking.setEnd(time.plusHours(1));
        return booking;
    }

    /**
     * @return бронь заканчивается одновременно с началом уже имеющейся брони
     */
    public static Booking testCrossWithBookingDtoIn5() {
        Booking booking = new Booking();
        booking.setId(8L);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.plusHours(5));
        booking.setEnd(time.plusHours(6));
        return booking;
    }

    public static Booking testFutureBooking2() {
        Booking booking = new Booking();
        booking.setId(5L);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.plusDays(2));
        booking.setEnd(time.plusDays(3));
        return booking;
    }

    public static Booking testFutureWaitingBooking3() {
        Booking booking = new Booking();
        booking.setId(6L);
        booking.setStatus(StatusOfBooking.WAITING);
        booking.setStart(time.plusDays(3));
        booking.setEnd(time.plusDays(4));
        return booking;
    }

    public static BookingDtoOut testBookingDtoOut1() {
        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(1L);
        bookingDtoOut.setStart(time.plusHours(1));
        bookingDtoOut.setEnd(time.plusHours(5));
        bookingDtoOut.setStatus(StatusOfBooking.WAITING);
        return bookingDtoOut;
    }

    public static BookingDtoOut testBookingDtoOut2() {
        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(2L);
        bookingDtoOut.setStart(time.plusHours(6));
        bookingDtoOut.setEnd(time.plusHours(9));
        bookingDtoOut.setStatus(StatusOfBooking.APPROVED);
        return bookingDtoOut;
    }

    public static BookingDtoIn testBookingDtoIn() {
        BookingDtoIn bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setStart(time.plusHours(1));
        bookingDtoIn.setEnd(time.plusHours(5));
        bookingDtoIn.setItemId(4L);
        return bookingDtoIn;
    }

    public static BookingDtoIn testBookingDtoIn2() {
        BookingDtoIn bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setStart(time.plusHours(1));
        bookingDtoIn.setEnd(time.plusHours(2));
        return bookingDtoIn;
    }
}
