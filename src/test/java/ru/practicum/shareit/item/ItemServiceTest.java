package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IncorrectException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.DataTest;

import java.time.LocalDateTime;
import java.util.Collections;
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
class ItemServiceTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository requestRepository;
    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void createItemTest() {
        User owner = DataTest.testUser1();
        Item item = DataTest.testItem4();
        item.setOwner(owner);
        ItemDto itemDto = new ItemDto();

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto createdItemDto = itemService.createItem(itemDto, owner.getId());
        assertThat(createdItemDto.getId(), equalTo(item.getId()));
        assertThat(createdItemDto.getName(), equalTo(item.getName()));
        assertThat(createdItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(createdItemDto.getAvailable(), equalTo(item.getAvailable()));

        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void createItemOnRequestTest() {
        User owner = DataTest.testUser1();
        ItemRequest itemRequest = DataTest.testItemRequest1();
        Item item = DataTest.testItem1();
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(itemRequest.getId());

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(requestRepository.findById(eq(itemRequest.getId()))).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);


        ItemDto createdItemDto = itemService.createItem(itemDto, owner.getId());
        assertThat(createdItemDto.getId(), equalTo(item.getId()));
        assertThat(createdItemDto.getName(), equalTo(item.getName()));
        assertThat(createdItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(createdItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(createdItemDto.getRequestId(), equalTo(itemRequest.getId()));

        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(requestRepository, times(1)).findById(eq(itemRequest.getId()));
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(userRepository, itemRepository);
    }


    @Test
    void createCommentTest() {
        User author = DataTest.testUser1();
        Item item = DataTest.testItem4();
        Comment comment = DataTest.testComment1();
        comment.setAuthor(author);
        comment.setItem(item);
        CommentDto commentDto = new CommentDto();


        when(userRepository.findById(eq(author.getId()))).thenReturn(Optional.of(author));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBookerIdAndEndBeforeAndStatus(
                eq(item.getId()), eq(author.getId()), any(LocalDateTime.class), any(StatusOfBooking.class)))
                .thenReturn(List.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto createdCommentDto = itemService.createComment(author.getId(), commentDto, item.getId());

        assertThat(createdCommentDto.getId(), equalTo(comment.getId()));
        assertThat(createdCommentDto.getText(), equalTo(comment.getText()));
        assertThat(createdCommentDto.getAuthorName(), equalTo(author.getName()));

        verify(userRepository, times(1)).findById(eq(author.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findAllByItemIdAndBookerIdAndEndBeforeAndStatus(
                eq(item.getId()), eq(author.getId()), any(LocalDateTime.class), eq(StatusOfBooking.APPROVED)
        );
        verify(commentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void createCommentWithoutBookingThrowExceptionTest() {
        User author = DataTest.testUser1();
        Item item = DataTest.testItem4();
        Comment comment = DataTest.testComment1();
        comment.setAuthor(author);
        comment.setItem(item);
        CommentDto commentDto = new CommentDto();


        when(userRepository.findById(eq(author.getId()))).thenReturn(Optional.of(author));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndBookerIdAndEndBeforeAndStatus(
                eq(item.getId()), eq(author.getId()), any(LocalDateTime.class), any(StatusOfBooking.class)))
                .thenReturn(Collections.emptyList());

        IncorrectException exception = assertThrows(
                IncorrectException.class,
                () -> itemService.createComment(author.getId(), commentDto, item.getId()),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Нельзя оставить отзыв на вещь которую не бронировали", exception.getMessage());

        verify(userRepository, times(1)).findById(eq(author.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findAllByItemIdAndBookerIdAndEndBeforeAndStatus(
                eq(item.getId()), eq(author.getId()), any(LocalDateTime.class), any(StatusOfBooking.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void updateItemTest() {
        User owner = DataTest.testUser2();
        Item item = DataTest.testItem1();
        item.setOwner(owner);
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updatedDto = itemService.updateItem(itemDto, item.getId(), owner.getId());

        assertThat(updatedDto.getId(), equalTo(item.getId()));
        assertThat(updatedDto.getName(), equalTo(item.getName()));
        assertThat(updatedDto.getDescription(), equalTo(item.getDescription()));
        assertThat(updatedDto.getAvailable(), equalTo(item.getAvailable()));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void updateItemNotOwnerThrowExceptionTest() {
        User owner = DataTest.testUser2();
        User user = DataTest.testUser1();
        Item item = DataTest.testItem1();
        item.setOwner(owner);
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> itemService.updateItem(itemDto, item.getId(), user.getId()),
                "Должно быть выброшено исключение"
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        assertEquals("Пользователь не является владельцем объекта", exception.getMessage());


        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByIdByOwnerTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser3();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking lastBooking = DataTest.testLastBooking1();
        lastBooking.setBooker(booker);
        lastBooking.setItem(item);

        Booking nextBooking = DataTest.testFutureBooking1();
        nextBooking.setBooker(booker);
        nextBooking.setItem(item);

        Comment comment1 = DataTest.testComment1();
        comment1.setItem(item);
        comment1.setAuthor(booker);

        Comment comment2 = DataTest.testComment2();
        comment2.setItem(item);
        comment2.setAuthor(booker);

        List<Comment> comments = List.of(comment1, comment2);

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(eq(item.getId()))).thenReturn(comments);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                eq(item.getId()), any(StatusOfBooking.class), any(LocalDateTime.class)))
                .thenReturn(nextBooking);
        when(bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(
                eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(lastBooking);

        ItemDto itemDto = itemService.getById(item.getId(), owner.getId());
        assertThat(itemDto.getId(), equalTo(item.getId()));
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));

        assertThat(itemDto.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(itemDto.getNextBooking().getBookerId(), equalTo(booker.getId()));
        assertThat(itemDto.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(itemDto.getLastBooking().getBookerId(), equalTo(booker.getId()));

        assertThat(itemDto.getComments().size(), equalTo(2));
        assertThat(itemDto.getComments().get(0).getId(), equalTo(comment1.getId()));
        assertThat(itemDto.getComments().get(0).getText(), equalTo(comment1.getText()));
        assertThat(itemDto.getComments().get(0).getAuthorName(), equalTo(booker.getName()));
        assertThat(itemDto.getComments().get(1).getId(), equalTo(comment2.getId()));
        assertThat(itemDto.getComments().get(1).getText(), equalTo(comment2.getText()));
        assertThat(itemDto.getComments().get(1).getAuthorName(), equalTo(booker.getName()));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(commentRepository, times(1)).findAllByItemId(eq(item.getId()));
        verify(bookingRepository, times(1)).findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                eq(item.getId()), any(StatusOfBooking.class), any(LocalDateTime.class)
        );
        verify(bookingRepository, times(1)).findFirstByItemIdAndStartBeforeOrderByStartDesc(
                eq(item.getId()), any(LocalDateTime.class)
        );
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getByIdByUserTest() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser3();
        User user = DataTest.testUser2();

        Item item = DataTest.testItem1();
        item.setOwner(owner);

        Booking lastBooking = DataTest.testLastBooking1();
        lastBooking.setBooker(booker);
        lastBooking.setItem(item);

        Booking nextBooking = DataTest.testFutureBooking1();
        nextBooking.setBooker(booker);
        nextBooking.setItem(item);

        Comment comment1 = DataTest.testComment1();
        comment1.setItem(item);
        comment1.setAuthor(booker);

        Comment comment2 = DataTest.testComment2();
        comment2.setItem(item);
        comment2.setAuthor(booker);
        List<Comment> comments = List.of(comment1, comment2);

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(eq(item.getId()))).thenReturn(comments);

        ItemDto itemDto = itemService.getById(item.getId(), user.getId());

        assertThat(itemDto.getId(), equalTo(item.getId()));
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));

        assertThat(itemDto.getNextBooking(), equalTo(null));
        assertThat(itemDto.getLastBooking(), equalTo(null));

        assertThat(itemDto.getComments().size(), equalTo(2));
        assertThat(itemDto.getComments().get(0).getId(), equalTo(comment1.getId()));
        assertThat(itemDto.getComments().get(0).getText(), equalTo(comment1.getText()));
        assertThat(itemDto.getComments().get(0).getAuthorName(), equalTo(booker.getName()));
        assertThat(itemDto.getComments().get(1).getId(), equalTo(comment2.getId()));
        assertThat(itemDto.getComments().get(1).getText(), equalTo(comment2.getText()));
        assertThat(itemDto.getComments().get(1).getAuthorName(), equalTo(booker.getName()));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(commentRepository, times(1)).findAllByItemId(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getBySearchTest() {
        Item item1 = DataTest.testItem1();
        Item item2 = DataTest.testItem2();
        List<Item> items = List.of(item1, item2);

        String search = "школы волка";

        when(itemRepository.findAllBySearch(eq(search), any(Pageable.class))).thenReturn(items);

        List<ItemDto> itemDtoList = itemService.getBySearch(search, 0, 10);
        assertThat(itemDtoList.size(), equalTo(2));

        assertThat(itemDtoList.get(0).getId(), equalTo(item1.getId()));
        assertThat(itemDtoList.get(0).getName(), equalTo(item1.getName()));
        assertThat(itemDtoList.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDtoList.get(0).getAvailable(), equalTo(item1.getAvailable()));

        assertThat(itemDtoList.get(1).getId(), equalTo(item2.getId()));
        assertThat(itemDtoList.get(1).getName(), equalTo(item2.getName()));
        assertThat(itemDtoList.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(itemDtoList.get(1).getAvailable(), equalTo(item2.getAvailable()));

        verify(itemRepository, times(1)).findAllBySearch(eq(search), any(Pageable.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getBySearchWithoutTextTest() {
        List<ItemDto> itemDtoList = itemService.getBySearch("", 0, 10);
        assertThat(itemDtoList.size(), equalTo(0));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getAllByOwner() {
        User owner = DataTest.testUser1();
        User booker = DataTest.testUser3();

        Item item1 = DataTest.testItem1();
        item1.setOwner(owner);
        Item item2 = DataTest.testItem2();
        item2.setOwner(owner);
        List<Item> items = List.of(item1, item2);
        List<Long> itemIds = List.of(item1.getId(), item2.getId());

        Booking lastBooking = DataTest.testLastBooking1();
        lastBooking.setBooker(booker);
        lastBooking.setItem(item1);

        Booking nextBooking = DataTest.testFutureBooking1();
        nextBooking.setBooker(booker);
        nextBooking.setItem(item1);
        List<Booking> item1Bookings = List.of(lastBooking, nextBooking);

        Comment comment1 = DataTest.testComment1();
        comment1.setItem(item1);
        comment1.setAuthor(booker);

        Comment comment2 = DataTest.testComment2();
        comment2.setItem(item1);
        comment2.setAuthor(booker);
        List<Comment> comments = List.of(comment1, comment2);

        when(itemRepository.findALLByOwnerIdOrderByIdAsc(eq(owner.getId()), any(Pageable.class))).thenReturn(items);
        when(bookingRepository.findAllByItemIdIn(itemIds)).thenReturn(item1Bookings);
        when(commentRepository.findAllByItemIdIn(itemIds)).thenReturn(comments);

        List<ItemDto> itemDtoList = itemService.getAllByOwner(owner.getId(), 0, 10);

        assertThat(itemDtoList.size(), equalTo(2));
        assertThat(itemDtoList.get(0).getId(), equalTo(item1.getId()));
        assertThat(itemDtoList.get(0).getName(), equalTo(item1.getName()));
        assertThat(itemDtoList.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDtoList.get(0).getAvailable(), equalTo(item1.getAvailable()));

        assertThat(itemDtoList.get(0).getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(itemDtoList.get(0).getNextBooking().getBookerId(), equalTo(booker.getId()));
        assertThat(itemDtoList.get(0).getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(itemDtoList.get(0).getLastBooking().getBookerId(), equalTo(booker.getId()));

        assertThat(itemDtoList.get(0).getComments().size(), equalTo(2));
        assertThat(itemDtoList.get(0).getComments().get(0).getId(), equalTo(comment1.getId()));
        assertThat(itemDtoList.get(0).getComments().get(0).getText(), equalTo(comment1.getText()));
        assertThat(itemDtoList.get(0).getComments().get(0).getAuthorName(), equalTo(booker.getName()));
        assertThat(itemDtoList.get(0).getComments().get(1).getId(), equalTo(comment2.getId()));
        assertThat(itemDtoList.get(0).getComments().get(1).getText(), equalTo(comment2.getText()));
        assertThat(itemDtoList.get(0).getComments().get(1).getAuthorName(), equalTo(booker.getName()));

        assertThat(itemDtoList.get(1).getId(), equalTo(item2.getId()));
        assertThat(itemDtoList.get(1).getName(), equalTo(item2.getName()));
        assertThat(itemDtoList.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(itemDtoList.get(1).getAvailable(), equalTo(item2.getAvailable()));

        assertThat(itemDtoList.get(1).getNextBooking(), equalTo(null));
        assertThat(itemDtoList.get(1).getLastBooking(), equalTo(null));

        assertThat(itemDtoList.get(1).getComments().size(), equalTo(0));

        verify(itemRepository, times(1))
                .findALLByOwnerIdOrderByIdAsc(eq(owner.getId()), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemIdIn(itemIds);
        verify(commentRepository, times(1)).findAllByItemIdIn(itemIds);
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void deleteById() {
        itemService.deleteById(1);

        verify(itemRepository, times(1)).deleteById(eq(1L));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }
}