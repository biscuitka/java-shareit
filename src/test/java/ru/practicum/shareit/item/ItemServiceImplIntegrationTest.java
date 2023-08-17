package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.DataIntegrationTest;
import ru.practicum.shareit.util.TestConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceImplIntegrationTest {
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;

    @Test
    void createItem() {
        User user = DataIntegrationTest.testUser1();
        userRepository.save(user);
        ItemDto itemDto = DataIntegrationTest.testItemDto1();
        ItemDto createdItemDto = itemService.createItem(itemDto, user.getId());
        Item createdItem = itemRepository.findById(createdItemDto.getId()).orElse(null);

        assertNotNull(createdItem);
        assertEquals("меч Школы Грифона", createdItem.getName());
        assertEquals(user, createdItem.getOwner());
    }

    @Test
    void createComment() {
        User owner = DataIntegrationTest.testUser1();
        userRepository.save(owner);
        User author = DataIntegrationTest.testUser2();
        userRepository.save(author);
        Item item = DataIntegrationTest.testItem1();
        item.setOwner(owner);
        itemRepository.save(item);
        Booking booking = DataIntegrationTest.testLastBooking1();
        booking.setItem(item);
        booking.setBooker(author);
        bookingRepository.save(booking);

        CommentDto commentDto = DataIntegrationTest.testCommentDto();
        CommentDto createdCommentDto = itemService.createComment(author.getId(), commentDto, item.getId());
        Comment createdComment = commentRepository.findById(createdCommentDto.getId()).orElse(null);

        assertNotNull(createdComment);
        assertEquals(1, createdComment.getId());
        assertEquals("Замечательные штаны, рекомендую", createdComment.getText());
        assertEquals(author, createdComment.getAuthor());
    }

    @Test
    void updateItem() {
        User owner = DataIntegrationTest.testUser1();
        userRepository.save(owner);
        Item item = DataIntegrationTest.testItem1();
        item.setOwner(owner);
        itemRepository.save(item);
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Штаны");
        itemDto.setDescription("Отличные штаны школы волка");

        ItemDto updatedItemDto = itemService.updateItem(itemDto, item.getId(), owner.getId());
        Item updatedItem = itemRepository.findById(updatedItemDto.getId()).orElse(null);

        assertNotNull(updatedItem);
        assertEquals("Штаны", updatedItem.getName());
        assertEquals("Отличные штаны школы волка", updatedItem.getDescription());
    }

    @Test
    void getById() {
        User owner = DataIntegrationTest.testUser1();
        userRepository.save(owner);
        User author = DataIntegrationTest.testUser2();
        userRepository.save(author);
        Item item = DataIntegrationTest.testItem1();
        item.setOwner(owner);
        itemRepository.save(item);
        Comment comment = DataIntegrationTest.testComment1();
        comment.setItem(item);
        comment.setAuthor(author);
        commentRepository.save(comment);

        ItemDto itemDto = itemService.getById(item.getId(), owner.getId());

        assertNotNull(itemDto);
        assertEquals("Штаны", itemDto.getName());
        assertEquals("Классные шмотки", itemDto.getComments().get(0).getText());
    }

    @Test
    void getBySearch() {
        String search = "ШТаНЫ";
        String search2 = "грОССмейстерский";
        User owner = DataIntegrationTest.testUser1();
        userRepository.save(owner);
        User user = DataIntegrationTest.testUser2();
        userRepository.save(user);
        Item item = DataIntegrationTest.testItem1();
        item.setOwner(owner);
        Item item2 = DataIntegrationTest.testItem2();
        item2.setOwner(owner);
        Item item3 = DataIntegrationTest.testItem3();
        item3.setOwner(owner);
        itemRepository.saveAll(List.of(item, item2, item3));

        List<ItemDto> foundItems = itemService.getBySearch(search, TestConstants.PAGEABLE);
        List<ItemDto> foundItems2 = itemService.getBySearch(search2, TestConstants.PAGEABLE);

        assertThat(foundItems).hasSize(2);
        assertThat(foundItems2).hasSize(1);
        assertEquals("Штаны", foundItems.get(0).getName());
        assertEquals("Махакамские штаны", foundItems.get(0).getDescription());
        assertEquals("Штаны", foundItems.get(1).getName());
        assertEquals("Отличные штаны школы волка", foundItems.get(1).getDescription());
        assertEquals("Доспех", foundItems2.get(0).getName());
        assertEquals("Гроссмейстерский доспех школы волка", foundItems2.get(0).getDescription());
    }

    @Test
    void getAllByOwner() {
        User owner = DataIntegrationTest.testUser1();
        userRepository.save(owner);
        Item item = DataIntegrationTest.testItem1();
        item.setOwner(owner);
        Item item2 = DataIntegrationTest.testItem2();
        item2.setOwner(owner);
        Item item3 = DataIntegrationTest.testItem3();
        item3.setOwner(owner);
        itemRepository.saveAll(List.of(item, item2, item3));

        List<ItemDto> itemDtoList = itemService.getAllByOwner(owner.getId(), TestConstants.PAGEABLE);

        assertThat(itemDtoList).hasSize(3);
    }

    @Test
    void deleteById() {
        User user = DataIntegrationTest.testUser1();
        userRepository.save(user);
        Item item = DataIntegrationTest.testItem1();
        item.setOwner(user);
        itemRepository.save(item);

        itemService.deleteById(item.getId());
        Item deletedItem = itemRepository.findById(item.getId()).orElse(null);

        assertNull(deletedItem);
    }
}