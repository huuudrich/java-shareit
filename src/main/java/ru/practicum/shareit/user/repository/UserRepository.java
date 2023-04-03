package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User createUser(User user) throws DuplicateEmailFoundException;

    User updateUser(Long userId, User user) throws DuplicateEmailFoundException;

    void deleteUser(Long userId);

    User getUser(Long userId);

    List<User> getAllUsers();
}
