package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users;
    private final AtomicLong idCounter;

    public UserRepositoryImpl() {
        this.users = new HashMap<>();
        this.idCounter = new AtomicLong(1);
    }

    @Override
    public User createUser(User user) throws DuplicateEmailFoundException {
        if (findByEmail(user.getEmail())) {
            throw new DuplicateEmailFoundException("This email is already in use");
        }
        long newId = idCounter.getAndIncrement();
        user.setId(newId);
        findByEmail(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) throws DuplicateEmailFoundException {
        User oldUser = users.get(userId);
        if (findByEmail(user.getEmail()) && !oldUser.getEmail().equals(user.getEmail())) {
            throw new DuplicateEmailFoundException("This email is already in use");
        }
        if (users.containsKey(userId)) {
            if (user.getEmail() == null) {
                user.setEmail(oldUser.getEmail());
            }
            if (user.getName() == null) {
                user.setName(oldUser.getName());
            }
            users.put(userId, user);
            user.setId(userId);
            return user;
        } else {
            throw new NotFoundException(String.format("User not found with id: %d", userId));
        }
    }

    @Override
    public void deleteUser(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
        } else {
            throw new NotFoundException(String.format("User not found with id: %d", userId));
        }
    }

    @Override
    public User getUser(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException(String.format("User not found with id: %d", userId));
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Map<Long, User> getMapUsers() {
        return users;
    }

    private Boolean findByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

}
