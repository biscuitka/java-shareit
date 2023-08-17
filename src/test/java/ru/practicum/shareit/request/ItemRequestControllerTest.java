package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.DataTest;
import ru.practicum.shareit.util.TestConstants;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createRequestTest() throws Exception {
        RequestDtoIn requestDtoIn = DataTest.testRequestDtoIn();
        RequestDtoOut requestDtoOut = DataTest.testRequestDtoOut();

        when(itemRequestService.createRequest(eq(DataTest.userId), any(RequestDtoIn.class)))
                .thenReturn(requestDtoOut);

        mockMvc.perform(post("/requests")
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId)
                        .content(objectMapper.writeValueAsString(requestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDtoOut.getId()))
                .andExpect(jsonPath("$.description").value(requestDtoOut.getDescription()))
                .andExpect(jsonPath("$.created")
                        .value(requestDtoOut.getCreated().format(TestConstants.DATE_TIME_FORMATTER)));

        verify(itemRequestService, times(1))
                .createRequest(eq(DataTest.userId), any(RequestDtoIn.class));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getByIdTest() throws Exception {
        RequestDtoWithItemsOut requestDtoWithItemsOut = DataTest.testRequestDtoWithItemsOut1();

        when(itemRequestService.getById(eq(DataTest.userId), eq(requestDtoWithItemsOut.getId())))
                .thenReturn(requestDtoWithItemsOut);

        mockMvc.perform(get("/requests/" + requestDtoWithItemsOut.getId())
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDtoWithItemsOut.getId()))
                .andExpect(jsonPath("$.description").value(requestDtoWithItemsOut.getDescription()))
                .andExpect(jsonPath("$.items").value(requestDtoWithItemsOut.getItems()))
                .andExpect(jsonPath("$.created")
                        .value(requestDtoWithItemsOut.getCreated().format(TestConstants.DATE_TIME_FORMATTER)));

    }

    @Test
    void getAllByOwnerTest() throws Exception {
        RequestDtoWithItemsOut requestDto1 = DataTest.testRequestDtoWithItemsOut1();
        RequestDtoWithItemsOut requestDto2 = DataTest.testRequestDtoWithItemsOut2();
        List<RequestDtoWithItemsOut> requestDtoWithItemsOutList = List.of(requestDto1, requestDto2);

        when(itemRequestService.getAllByOwner(eq(DataTest.userId), any(Pageable.class)))
                .thenReturn(requestDtoWithItemsOutList);

        mockMvc.perform(get("/requests")
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto1.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto1.getDescription()))
                .andExpect(jsonPath("$[0].items").value(requestDto1.getItems()))
                .andExpect(jsonPath("$[0].created")
                        .value(requestDto1.getCreated().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[1].id").value(requestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(requestDto2.getDescription()))
                .andExpect(jsonPath("$[1].items").value(requestDto2.getItems()))
                .andExpect(jsonPath("$[1].created")
                        .value(requestDto2.getCreated().format(TestConstants.DATE_TIME_FORMATTER)));

        verify(itemRequestService, times(1)).getAllByOwner(eq(DataTest.userId), any(Pageable.class));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getAllRequestsTest() throws Exception {
        RequestDtoWithItemsOut requestDto1 = DataTest.testRequestDtoWithItemsOut1();
        RequestDtoWithItemsOut requestDto2 = DataTest.testRequestDtoWithItemsOut2();
        List<RequestDtoWithItemsOut> requestDtoWithItemsOutList = List.of(requestDto1, requestDto2);

        when(itemRequestService.getAll(eq(DataTest.userId), any(Pageable.class)))
                .thenReturn(requestDtoWithItemsOutList);

        mockMvc.perform(get("/requests/all")
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto1.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto1.getDescription()))
                .andExpect(jsonPath("$[0].items").value(requestDto1.getItems()))
                .andExpect(jsonPath("$[0].created")
                        .value(requestDto1.getCreated().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[1].id").value(requestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(requestDto2.getDescription()))
                .andExpect(jsonPath("$[1].items").value(requestDto2.getItems()))
                .andExpect(jsonPath("$[1].created")
                        .value(requestDto2.getCreated().format(TestConstants.DATE_TIME_FORMATTER)));

        verify(itemRequestService, times(1)).getAll(eq(DataTest.userId), any(Pageable.class));
        verifyNoMoreInteractions(itemRequestService);
    }
}