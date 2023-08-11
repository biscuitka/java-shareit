package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.DataTest;
import ru.practicum.shareit.util.TestConstants;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemMapperTest {
    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;
    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;

    @Test
    void itemDtoTest() throws IOException {
        BookingDtoShort nextBooking = new BookingDtoShort();
        nextBooking.setId(1L);
        nextBooking.setBookerId(3L);

        ItemDto itemDto = DataTest.testItemDto1();
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(nextBooking);
        itemDto.setRequestId(4L);
        itemDto.setComments(new ArrayList<>());

        JsonContent<ItemDto> content = itemDtoJacksonTester.write(itemDto);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("меч Школы Грифона");
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo("Улучшенный стальной меч Школы Грифона");
        assertThat(content).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(content).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.lastBooking").isEqualTo(null);
        assertThat(content).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(3);
        assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(4);
        assertThat(content).extractingJsonPathArrayValue("$.comments").isEmpty();
    }

    @Test
    void commentDtoTest() throws IOException {
        CommentDto commentDto = DataTest.testCommentDto();

        JsonContent<CommentDto> content = commentDtoJacksonTester.write(commentDto);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(content).extractingJsonPathStringValue("$.text")
                .isEqualTo("Замечательные штаны, рекомендую");
        assertThat(content).extractingJsonPathStringValue("$.authorName").isEqualTo("Весемир");
        assertThat(content).extractingJsonPathStringValue("$.created")
                .isEqualTo(DataTest.time.format(TestConstants.DATE_TIME_FORMATTER));
    }

}