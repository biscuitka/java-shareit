package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class DataIntegrationTest {
    public static LocalDateTime time = LocalDateTime.now().withNano(0);

    public static User testUser1() {
        User user = new User();
        user.setName("Ciri");
        user.setEmail("princessOfCintra@ya.ru");
        return user;
    }

    public static User testUser2() {
        User user = new User();
        user.setName("Julian");
        user.setEmail("ranunculus@ya.ru");
        return user;
    }

    public static ItemDto testItemDto1() {
        ItemDto item = new ItemDto();
        item.setName("меч Школы Грифона");
        item.setDescription("Улучшенный стальной меч Школы Грифона");
        item.setAvailable(true);
        return item;
    }

    public static Item testItem1() {
        Item item = new Item();
        item.setName("Штаны");
        item.setDescription("Махакамские штаны");
        item.setAvailable(true);
        return item;
    }

    public static Item testItem2() {
        Item item = new Item();
        item.setName("Штаны");
        item.setDescription("Отличные штаны школы волка");
        item.setAvailable(true);
        return item;
    }

    public static Item testItem3() {
        Item item = new Item();
        item.setName("Доспех");
        item.setDescription("Гроссмейстерский доспех школы волка");
        item.setAvailable(true);
        return item;
    }

    public static CommentDto testCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Замечательные штаны, рекомендую");
        return commentDto;
    }

    public static Comment testComment1() {
        Comment comment = new Comment();
        comment.setText("Классные шмотки");
        comment.setCreated(time);
        return comment;
    }

    public static Booking testLastBooking1() {
        Booking booking = new Booking();
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.minusDays(2));
        booking.setEnd(time.minusDays(1));
        return booking;
    }

    public static ItemRequest testItemRequest1() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужен прочный доспех");
        itemRequest.setCreated(time);
        return itemRequest;
    }

    public static ItemRequest testItemRequest2() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Ищу прочные штаны для битвы с гулем");
        itemRequest.setCreated(time);
        return itemRequest;
    }

    public static ItemRequest testItemRequest3() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Есть у кого штаны для званого вечера?");
        itemRequest.setCreated(time);
        return itemRequest;
    }

    public static Booking testLastBooking2() {
        Booking booking = new Booking();
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.minusDays(2));
        booking.setEnd(time.minusDays(1));
        return booking;
    }

    public static Booking testFutureBooking1() {
        Booking booking = new Booking();
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(time.plusDays(1));
        booking.setEnd(time.plusDays(2));
        return booking;
    }
}
