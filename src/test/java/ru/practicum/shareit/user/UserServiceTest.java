package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.DataTest;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void createUserTest() {
        User user = DataTest.testUser1();
        UserDto dto = new UserDto();

        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto savedDto = userService.createUser(dto);

        assertThat(savedDto.getId(), equalTo(user.getId()));
        assertThat(savedDto.getName(), equalTo(user.getName()));
        assertThat(savedDto.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUserThrowExceptionTest() {
        UserDto dto = new UserDto();

        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("DataIntegrityViolationException"));

        ResponseStatusException e = null;
        try {
            userService.createUser(dto);
            fail("Должно быть выброшено исключение");
        } catch (ResponseStatusException ex) {
            e = ex;
        }
        assertThat(e.getStatus(), equalTo(HttpStatus.CONFLICT));
        assertThat(e.getReason(), equalTo("Ошибка при создании пользователя"));

        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserTest() {
        User user = DataTest.testUser1();
        UserDto dto = new UserDto();

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto updatedDto = userService.updateUser(dto, user.getId());

        assertThat(updatedDto.getId(), equalTo(user.getId()));
        assertThat(updatedDto.getName(), equalTo(user.getName()));
        assertThat(updatedDto.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserThrowExceptionTest() {
        UserDto dto = new UserDto();
        User user = DataTest.testUser1();

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("DataIntegrityViolationException"));

        ResponseStatusException e = null;
        try {
            userService.updateUser(dto, user.getId());
            fail("Должно быть выброшено исключение");
        } catch (ResponseStatusException ex) {
            e = ex;
        }
        assertThat(e.getStatus(), equalTo(HttpStatus.CONFLICT));
        assertThat(e.getReason(), equalTo("Ошибка при обновлении пользователя"));

        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById() {
        User user = DataTest.testUser1();

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(user.getId());

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers() {
        User user1 = DataTest.testUser1();
        User user2 = DataTest.testUser2();
        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> userDtoList = userService.getAllUsers();

        assertThat(userDtoList.size(), equalTo(users.size()));
        assertThat(userDtoList.get(0).getId(), equalTo(user1.getId()));
        assertThat(userDtoList.get(0).getName(), equalTo(user1.getName()));
        assertThat(userDtoList.get(0).getEmail(), equalTo(user1.getEmail()));

        assertThat(userDtoList.get(1).getId(), equalTo(user2.getId()));
        assertThat(userDtoList.get(1).getName(), equalTo(user2.getName()));
        assertThat(userDtoList.get(1).getEmail(), equalTo(user2.getEmail()));

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(DataTest.userId);

        verify(userRepository, times(1)).deleteById(eq(DataTest.userId));
        verifyNoMoreInteractions(userRepository);
    }
}