package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.DataTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class UserMapperTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void userDtoTest() throws IOException {
        UserDto userDto = DataTest.testUserDto1();

        JsonContent<UserDto> content = jacksonTester.write(userDto);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("Geralt");
        assertThat(content).extractingJsonPathStringValue("$.email")
                .isEqualTo("butcherOfBlaviken@ya.ru");
    }
}
