package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static java.lang.String.format;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        return userMapper.toUserDto(userRepository.save(user));
    }

    public UserDto updateUser(Long userId, User user) {
        log.info("Updating user with id: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(format("User with id %d not found", userId));
        }

        User existingUser = userMapper.toUser(getUser(userId));

        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        return userMapper.toUserDto(userRepository.save(existingUser));
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
        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(format("User with id %d not found", userId))));
    }

    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        return userMapper.userListToDto(userRepository.findAll());
    }
}
