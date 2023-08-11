package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.util.DataTest;
import ru.practicum.shareit.util.TestConstants;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingMapperTest {
    @Autowired
    private JacksonTester<BookingDtoOut> bookingDtoOutJacksonTester;

    @Test
    void bookingDtoOutTest() throws IOException {
        UserDtoShort userDto = new UserDtoShort();
        userDto.setId(8L);

        ItemDtoShort itemDto = new ItemDtoShort();
        itemDto.setId(47L);
        itemDto.setName("Кираса");

        BookingDtoOut bookingDtoOut = DataTest.testBookingDtoOut1();
        bookingDtoOut.setItem(itemDto);
        bookingDtoOut.setBooker(userDto);


        JsonContent<BookingDtoOut> content = bookingDtoOutJacksonTester.write(bookingDtoOut);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathNumberValue("$.item.id").isEqualTo(47);
        assertThat(content).extractingJsonPathStringValue("$.item.name").isEqualTo("Кираса");
        assertThat(content).extractingJsonPathNumberValue("$.booker.id").isEqualTo(8);
        assertThat(content).extractingJsonPathStringValue("$.status")
                .isEqualTo(StatusOfBooking.WAITING.toString());
        assertThat(content).extractingJsonPathStringValue("$.start")
                .isEqualTo(DataTest.time.plusHours(1).format(TestConstants.DATE_TIME_FORMATTER));
        assertThat(content).extractingJsonPathStringValue("$.end")
                .isEqualTo(DataTest.time.plusHours(5).format(TestConstants.DATE_TIME_FORMATTER));
    }
}