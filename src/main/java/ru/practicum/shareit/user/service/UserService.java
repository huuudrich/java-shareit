package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    UserRepository userRepository;

    public User createUser(User user) throws DuplicateEmailFoundException {
        return userRepository.createUser(user);
    }

    public User updateUser(Long userId, User user) throws DuplicateEmailFoundException {
        return userRepository.updateUser(userId, user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    public User getUser(Long userId) {
        return userRepository.getUser(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
