package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.DataTest;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceTest {

    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void createRequestTest() {
        User user = DataTest.testUser1();
        ItemRequest itemRequest = DataTest.testItemRequest1();
        RequestDtoIn requestDtoIn = new RequestDtoIn();

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        RequestDtoOut requestDtoOut = itemRequestService.createRequest(user.getId(), requestDtoIn);
        assertThat(requestDtoOut.getId(), equalTo(itemRequest.getId()));
        assertThat(requestDtoOut.getDescription(), equalTo(itemRequest.getDescription()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
        verifyNoMoreInteractions(requestRepository, userRepository);
    }

    @Test
    void getByIdTest() {
        User requester = DataTest.testUser1();
        ItemRequest itemRequest = DataTest.testItemRequest2();
        itemRequest.setRequester(requester);
        User owner = DataTest.testUser2();
        Item item = DataTest.testItem1();
        item.setOwner(owner);
        item.setItemRequest(itemRequest);

        when(userRepository.findById(eq(requester.getId()))).thenReturn(Optional.of(requester));
        when(requestRepository.findById(eq(itemRequest.getId()))).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByItemRequestId(eq(itemRequest.getId()))).thenReturn(List.of(item));

        RequestDtoWithItemsOut requestDtoWithItemsOut = itemRequestService.getById(requester.getId(), itemRequest.getId());

        assertThat(requestDtoWithItemsOut.getId(), equalTo(itemRequest.getId()));
        assertThat(requestDtoWithItemsOut.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(requestDtoWithItemsOut.getItems().size(), equalTo(1));
        assertThat(requestDtoWithItemsOut.getItems().get(0).getId(), equalTo(item.getId()));
        assertThat(requestDtoWithItemsOut.getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(requestDtoWithItemsOut.getItems().get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(requestDtoWithItemsOut.getItems().get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(requestDtoWithItemsOut.getItems().get(0).getRequestId(), equalTo(item.getItemRequest().getId()));

        verify(userRepository, times(1)).findById(eq(requester.getId()));
        verify(requestRepository, times(1)).findById(eq(itemRequest.getId()));
        verify(itemRepository, times(1)).findAllByItemRequestId(eq(itemRequest.getId()));
        verifyNoMoreInteractions(requestRepository, userRepository, itemRepository);
    }

    @Test
    void getAllByOwnerTest() {
        User requester = DataTest.testUser1();

        ItemRequest itemRequest1 = DataTest.testItemRequest1();
        itemRequest1.setRequester(requester);
        ItemRequest itemRequest2 = DataTest.testItemRequest2();
        itemRequest2.setRequester(requester);

        User owner = DataTest.testUser2();

        Item item1 = DataTest.testItem1();
        item1.setOwner(owner);
        item1.setItemRequest(itemRequest1);
        Item item2 = DataTest.testItem2();
        item2.setOwner(owner);
        item2.setItemRequest(itemRequest2);

        List<ItemRequest> requestWithItemsList = List.of(itemRequest1, itemRequest2);
        assertThat(requestWithItemsList.size(), equalTo(2));

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(eq(requester.getId()), any(Pageable.class)))
                .thenReturn(requestWithItemsList);
        when(itemRepository.findAllByItemRequestIdIn(anyList())).thenReturn(List.of(item1, item2));

        List<RequestDtoWithItemsOut> requestDtoWithItemsOutList = itemRequestService
                .getAllByOwner(requester.getId(), 0, 10);

        assertThat(requestDtoWithItemsOutList.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(requestDtoWithItemsOutList.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().size(), equalTo(1));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getRequestId(), equalTo(item1.getItemRequest().getId()));

        assertThat(requestDtoWithItemsOutList.get(1).getId(), equalTo(itemRequest2.getId()));
        assertThat(requestDtoWithItemsOutList.get(1).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().size(), equalTo(1));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getId(), equalTo(item2.getId()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getName(), equalTo(item2.getName()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getDescription(), equalTo(item2.getDescription()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getAvailable(), equalTo(item2.getAvailable()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getRequestId(), equalTo(item2.getItemRequest().getId()));

        verify(userRepository, times(1)).findById(eq(requester.getId()));
        verify(requestRepository, times(1))
                .findAllByRequesterIdOrderByCreatedDesc(eq(requester.getId()), any(Pageable.class));
        verify(itemRepository, times(1)).findAllByItemRequestIdIn(anyList());
        verifyNoMoreInteractions(requestRepository, userRepository, itemRepository);
    }

    @Test
    void getAllTest() {
        User requester = DataTest.testUser1();

        ItemRequest itemRequest1 = DataTest.testItemRequest1();
        itemRequest1.setRequester(requester);
        ItemRequest itemRequest2 = DataTest.testItemRequest2();
        itemRequest2.setRequester(requester);

        User owner = DataTest.testUser2();

        Item item1 = DataTest.testItem1();
        item1.setOwner(owner);
        item1.setItemRequest(itemRequest1);
        Item item2 = DataTest.testItem2();
        item2.setOwner(owner);
        item2.setItemRequest(itemRequest2);

        List<ItemRequest> requestWithItemsList = List.of(itemRequest1, itemRequest2);
        assertThat(requestWithItemsList.size(), equalTo(2));

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(requester));
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(eq(owner.getId()), any(Pageable.class)))
                .thenReturn(requestWithItemsList);
        when(itemRepository.findAllByItemRequestIdIn(anyList())).thenReturn(List.of(item1, item2));

        List<RequestDtoWithItemsOut> requestDtoWithItemsOutList = itemRequestService
                .getAll(owner.getId(), 0, 10);

        assertThat(requestDtoWithItemsOutList.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(requestDtoWithItemsOutList.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().size(), equalTo(1));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(requestDtoWithItemsOutList.get(0).getItems().get(0).getRequestId(), equalTo(item1.getItemRequest().getId()));

        assertThat(requestDtoWithItemsOutList.get(1).getId(), equalTo(itemRequest2.getId()));
        assertThat(requestDtoWithItemsOutList.get(1).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().size(), equalTo(1));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getId(), equalTo(item2.getId()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getName(), equalTo(item2.getName()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getDescription(), equalTo(item2.getDescription()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getAvailable(), equalTo(item2.getAvailable()));
        assertThat(requestDtoWithItemsOutList.get(1).getItems().get(0).getRequestId(), equalTo(item2.getItemRequest().getId()));

        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(requestRepository, times(1))
                .findAllByRequesterIdNotOrderByCreatedDesc(eq(owner.getId()), any(Pageable.class));
        verify(itemRepository, times(1)).findAllByItemRequestIdIn(anyList());
        verifyNoMoreInteractions(requestRepository, userRepository, itemRepository);
    }
}