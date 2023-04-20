package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl {
    private final UserRepository userRepository;

    public User createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    public User updateUser(Long userId, User user) {
        log.info("Updating user with id: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id %d not found", userId));
        }

        User existingUser = getUser(userId);

        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Not found user with id: {}", userId);
            throw e;
        }
    }

    public User getUser(Long userId) {
        log.info("Getting user with id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d not found", userId)));
    }

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }
}
