package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
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
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    ItemRequestService requestService;
    @Autowired
    ItemRequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;


    @Test
    void getAllByOwner() {
        User owner = DataIntegrationTest.testUser1();
        User requester = DataIntegrationTest.testUser2();
        userRepository.saveAll(List.of(owner, requester));

        Item item = DataIntegrationTest.testItem1();
        item.setOwner(owner);
        Item item2 = DataIntegrationTest.testItem3();
        item2.setOwner(owner);
        Item item3 = DataIntegrationTest.testItem2();
        item3.setOwner(requester);

        ItemRequest itemRequest = DataIntegrationTest.testItemRequest1();
        itemRequest.setRequester(requester);
        ItemRequest itemRequest2 = DataIntegrationTest.testItemRequest2();
        itemRequest2.setRequester(requester);
        ItemRequest itemRequest3 = DataIntegrationTest.testItemRequest3();
        itemRequest3.setRequester(owner);
        requestRepository.saveAll(List.of(itemRequest, itemRequest2, itemRequest3));

        item.setItemRequest(itemRequest2);
        item2.setItemRequest(itemRequest);
        item3.setItemRequest(itemRequest3);
        itemRepository.saveAll(List.of(item, item2, item3));

        List<RequestDtoWithItemsOut> requestDtoWithItemsOutList = requestService
                .getAllByOwner(requester.getId(), TestConstants.PAGEABLE);

        assertThat(requestDtoWithItemsOutList).hasSize(2);
        assertEquals("Нужен прочный доспех", requestDtoWithItemsOutList.get(0).getDescription());
        assertEquals("Доспех", requestDtoWithItemsOutList.get(0).getItems().get(0).getName());

        assertEquals("Ищу прочные штаны для битвы с гулем", requestDtoWithItemsOutList.get(1).getDescription());
        assertEquals("Штаны", requestDtoWithItemsOutList.get(1).getItems().get(0).getName());
    }

    @Test
    void getAll() {
        User owner = DataIntegrationTest.testUser1();
        User requester = DataIntegrationTest.testUser2();
        userRepository.saveAll(List.of(owner, requester));

        Item item = DataIntegrationTest.testItem1();
        item.setOwner(owner);
        Item item2 = DataIntegrationTest.testItem3();
        item2.setOwner(owner);
        Item item3 = DataIntegrationTest.testItem2();
        item3.setOwner(requester);

        ItemRequest itemRequest = DataIntegrationTest.testItemRequest1();
        itemRequest.setRequester(requester);
        ItemRequest itemRequest2 = DataIntegrationTest.testItemRequest2();
        itemRequest2.setRequester(requester);
        ItemRequest itemRequest3 = DataIntegrationTest.testItemRequest3();
        itemRequest3.setRequester(owner);
        requestRepository.saveAll(List.of(itemRequest, itemRequest2, itemRequest3));

        item.setItemRequest(itemRequest2);
        item2.setItemRequest(itemRequest);
        item3.setItemRequest(itemRequest3);
        itemRepository.saveAll(List.of(item, item2, item3));

        List<RequestDtoWithItemsOut> requestDtoWithItemsOutList = requestService
                .getAll(requester.getId(), TestConstants.PAGEABLE);

        assertThat(requestDtoWithItemsOutList).hasSize(1);
        assertEquals("Есть у кого штаны для званого вечера?", requestDtoWithItemsOutList.get(0).getDescription());
        assertEquals("Штаны", requestDtoWithItemsOutList.get(0).getItems().get(0).getName());
    }
}