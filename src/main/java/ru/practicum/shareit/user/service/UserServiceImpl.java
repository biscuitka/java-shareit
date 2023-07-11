package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.fromDtoToUser(userDto);
        User createdUser = userDao.createUser(user);
        return UserMapper.fromUserToDto(createdUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User userToUpdate = UserMapper.fromDtoToUser(userDto);
        userToUpdate.setId(userId);
        User updatedUser = userDao.updateUser(userToUpdate, userId);

        return UserMapper.fromUserToDto(updatedUser);
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.fromUserToDto(userDao.getUserById(id));

    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers()
                .stream().map(UserMapper::fromUserToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long id) {
        userDao.deleteUserById(id);
    }
}
