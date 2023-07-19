package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User createUser(User user);

    User updateUser(User user, long userId);

    User getUserById(long userId);

    List<User> getAllUsers();

    void deleteUserById(long userId);
}
