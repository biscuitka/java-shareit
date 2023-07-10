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
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.fromDtoToUser(userDto);
        User createdUser = userDao.createUser(user);
        return userMapper.fromUserToDto(createdUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        //User oldUser = userDao.getUserById(userId);
        User userToUpdate = userMapper.fromDtoToUser(userDto);
        userToUpdate.setId(userId);
        User updatedUser = userDao.updateUser(userToUpdate, userId);

        return userMapper.fromUserToDto(updatedUser);
    }

    @Override
    public UserDto getUserById(long id) {
        return userMapper.fromUserToDto(userDao.getUserById(id));

    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers()
                .stream().map(userMapper::fromUserToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long id) {
        userDao.deleteUserById(id);
    }
}
