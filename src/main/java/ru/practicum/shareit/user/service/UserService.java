package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) throws DuplicateEmailFoundException {
        log.info("Creating user with email: {}", user.getEmail());
        try {
            return userRepository.createUser(user);
        } catch (DuplicateEmailFoundException e) {
            log.error("Duplicate email found: {}", user.getEmail(), e);
            throw e;
        }
    }

    public User updateUser(Long userId, User user) throws DuplicateEmailFoundException {
        log.info("Updating user with id: {}", userId);
        try {
            return userRepository.updateUser(userId, user);
        } catch (DuplicateEmailFoundException e) {
            log.error("Duplicate email found: {}", user.getEmail(), e);
            throw e;
        }
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);
        userRepository.deleteUser(userId);
    }

    public User getUser(Long userId) {
        log.info("Getting user with id: {}", userId);
        return userRepository.getUser(userId);
    }

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userRepository.getAllUsers();
    }
}
