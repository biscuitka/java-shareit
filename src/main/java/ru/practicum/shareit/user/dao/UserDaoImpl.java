package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> userStorage = new HashMap<>();
    private long idGenerator = 1;


    @Override
    public User createUser(User user) {
        if (userStorage.values().stream().noneMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            user.setId(idGenerator++);
            userStorage.put(user.getId(), user);
            return user;
        } else {
            throw new ExistException("Пользователь с такой почтой уже существует");
        }

    }

    @Override
    public User updateUser(User user, long userId) {
        if (userStorage.values().stream()
                .filter(oldUser -> oldUser.getId() != userId)
                .noneMatch(otherUser -> otherUser.getEmail().equals(user.getEmail()))) {
            User oldUser = userStorage.get(userId);
            if (user.getName() != null) {
                oldUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                oldUser.setEmail(user.getEmail());
            }
            return oldUser;
        } else {
            throw new ExistException("Email занят");
        }
    }

    @Override
    public User getUserById(long userId) {
        if (userStorage.containsKey(userId)) {
            return userStorage.get(userId);
        } else {
            throw new NotFoundException("Пользователь не найден по id " + userId);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public void deleteUserById(long userId) {
        userStorage.remove(userId);
    }
}
