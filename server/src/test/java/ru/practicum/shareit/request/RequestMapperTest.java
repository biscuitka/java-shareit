package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoRequested;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItemsOut;
import ru.practicum.shareit.util.DataTest;
import ru.practicum.shareit.util.TestConstants;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestMapperTest {
    @Autowired
    private JacksonTester<RequestDtoOut> dtoOutJacksonTester;

    @Autowired
    private JacksonTester<RequestDtoWithItemsOut> dtoWithItemsOutJacksonTester;

    @Test
    void requestDtoOutTest() throws IOException {
        RequestDtoOut requestDtoOut = DataTest.testRequestDtoOut();

        JsonContent<RequestDtoOut> content = dtoOutJacksonTester.write(requestDtoOut);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo("Нужен мастерский стальной меч Школы Грифона");
        assertThat(content).extractingJsonPathStringValue("$.created")
                .isEqualTo(DataTest.time.format(TestConstants.DATE_TIME_FORMATTER));
    }

    @Test
    void requestDtoWithItemsOutTest() throws IOException {

        ItemDtoRequested item = DataTest.testItemDtoRequested();
        RequestDtoWithItemsOut requestDtoOut = DataTest.testRequestDtoWithItemsOut3();
        item.setRequestId(requestDtoOut.getId());
        requestDtoOut.setItems(List.of(item));

        JsonContent<RequestDtoWithItemsOut> content = dtoWithItemsOutJacksonTester.write(requestDtoOut);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo("Нужен добротный доспех");
        assertThat(content).extractingJsonPathStringValue("$.created")
                .isEqualTo(DataTest.time.format(TestConstants.DATE_TIME_FORMATTER));
        assertThat(content).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(content).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(2);
        assertThat(content).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Доспех");
        assertThat(content).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo("Славный доспех рыцарей Пылающей Розы");
        assertThat(content).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(content).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(3);
    }
}