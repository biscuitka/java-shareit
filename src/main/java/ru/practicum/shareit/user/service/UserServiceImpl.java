package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.fromDtoToUser(userDto);
        User createdUser = userRepository.save(user);
        return UserMapper.fromUserToDto(createdUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        try {
            User userInRepo = getValidatedUser(userRepository, userId);
            if (userDto.getName() != null) {
                userInRepo.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                userInRepo.setEmail(userDto.getEmail());
            }
            User updatedUser = userRepository.save(userInRepo);
            return UserMapper.fromUserToDto(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(long userId) {
        User user = getValidatedUser(userRepository, userId);
        return UserMapper.fromUserToDto(user);

    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream().map(UserMapper::fromUserToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    public static User getValidatedUser(UserRepository userRepository, long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
