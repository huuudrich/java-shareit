package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(User user);

    UserDto updateUser(Long userId, User user);

    void deleteUser(Long userId);

    UserDto getUser(Long userId);

    List<UserDto> getAllUsers();

    void isExistingUser(Long userId);
}
