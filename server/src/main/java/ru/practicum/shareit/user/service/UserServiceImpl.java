package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

import static java.lang.String.format;
import static ru.practicum.shareit.user.utils.UserMapper.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserDto createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        return toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long userId, User user) {
        log.info("Updating user with id: {}", userId);

        User existingUser = toUser(getUser(userId));

        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        return toUserDto(userRepository.save(existingUser));
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);
        try {
            userRepository.deleteById(userId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(format("User with id %d not found", userId));
        }
    }

    public UserDto getUser(Long userId) {
        log.info("Getting user with id: {}", userId);
        return toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(format("User with id %d not found", userId))));
    }

    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        return userListToDto(userRepository.findAll());
    }

    public void isExistingUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(format("User with id %d not found", userId));
        }
    }
}
