package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.HeaderConstants;
import ru.practicum.shareit.util.DataTest;
import ru.practicum.shareit.util.TestConstants;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    final String bookingsPath = "/bookings";

    @Test
    void createBooking() throws Exception {
        BookingDtoIn bookingDtoIn = DataTest.testBookingDtoIn();
        BookingDtoOut bookingDtoOut = DataTest.testBookingDtoOut1();

        when(bookingService.createBooking(eq(DataTest.userId), any(BookingDtoIn.class)))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(post(bookingsPath)
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId)
                        .content(objectMapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$.start")
                        .value(bookingDtoOut.getStart().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end")
                        .value(bookingDtoOut.getEnd().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(bookingDtoOut.getStatus().toString()));

        verify(bookingService, times(1))
                .createBooking(eq(DataTest.userId), any(BookingDtoIn.class));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void updateStatus() throws Exception {
        BookingDtoOut bookingDtoOut = DataTest.testBookingDtoOut1();

        when(bookingService.updateStatus(eq(DataTest.userId), anyBoolean(), eq(bookingDtoOut.getId())))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(patch(bookingsPath + "/" + bookingDtoOut.getId())
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$.start")
                        .value(bookingDtoOut.getStart().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end")
                        .value(bookingDtoOut.getEnd().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(bookingDtoOut.getStatus().toString()));

        verify(bookingService, times(1))
                .updateStatus(eq(DataTest.userId), anyBoolean(), eq(bookingDtoOut.getId()));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBooking() throws Exception {
        BookingDtoOut bookingDtoOut = DataTest.testBookingDtoOut1();

        when(bookingService.getBooking(eq(DataTest.userId), eq(bookingDtoOut.getId())))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(get(bookingsPath + "/" + bookingDtoOut.getId())
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOut.getId()))
                .andExpect(jsonPath("$.start")
                        .value(bookingDtoOut.getStart().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end")
                        .value(bookingDtoOut.getEnd().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(bookingDtoOut.getStatus().toString()));

        verify(bookingService, times(1))
                .getBooking(eq(DataTest.userId), eq(bookingDtoOut.getId()));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllBookingByUser() throws Exception {
        BookingDtoOut bookingDtoOut1 = DataTest.testBookingDtoOut1();
        BookingDtoOut bookingDtoOut2 = DataTest.testBookingDtoOut2();
        List<BookingDtoOut> bookingDtoOutList = List.of(bookingDtoOut1, bookingDtoOut2);

        when(bookingService.getAllBookingByUser(eq(DataTest.userId), any(), anyInt(), anyInt()))
                .thenReturn(bookingDtoOutList);

        mockMvc.perform(get(bookingsPath)
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoOut1.getId()))
                .andExpect(jsonPath("$[0].start")
                        .value(bookingDtoOut1.getStart().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].end")
                        .value(bookingDtoOut1.getEnd().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].status").value(bookingDtoOut1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(bookingDtoOut2.getId()))
                .andExpect(jsonPath("$[1].start")
                        .value(bookingDtoOut2.getStart().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[1].end")
                        .value(bookingDtoOut2.getEnd().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[1].status").value(bookingDtoOut2.getStatus().toString()));

        verify(bookingService, times(1))
                .getAllBookingByUser(eq(DataTest.userId), any(), anyInt(), anyInt());

        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllBookingsByItemOwner() throws Exception {
        BookingDtoOut bookingDtoOut1 = DataTest.testBookingDtoOut1();
        BookingDtoOut bookingDtoOut2 = DataTest.testBookingDtoOut2();
        List<BookingDtoOut> bookingDtoOutList = List.of(bookingDtoOut1, bookingDtoOut2);

        when(bookingService.getAllBookingsByItemOwner(eq(DataTest.userId), any(), anyInt(), anyInt()))
                .thenReturn(bookingDtoOutList);

        mockMvc.perform(get(bookingsPath + "/owner")
                        .header(HeaderConstants.X_SHARER_USER_ID, DataTest.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDtoOut1.getId()))
                .andExpect(jsonPath("$[0].start")
                        .value(bookingDtoOut1.getStart().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].end")
                        .value(bookingDtoOut1.getEnd().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].status").value(bookingDtoOut1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(bookingDtoOut2.getId()))
                .andExpect(jsonPath("$[1].start")
                        .value(bookingDtoOut2.getStart().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[1].end")
                        .value(bookingDtoOut2.getEnd().format(TestConstants.DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[1].status").value(bookingDtoOut2.getStatus().toString()));

        verify(bookingService, times(1))
                .getAllBookingsByItemOwner(eq(DataTest.userId), any(), anyInt(), anyInt());

        verifyNoMoreInteractions(bookingService);
    }
}