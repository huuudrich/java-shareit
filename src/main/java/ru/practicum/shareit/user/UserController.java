package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws DuplicateEmailFoundException {
        return userServiceImpl.createUser(user);
    }

    @PatchMapping("{userId}")
    public User updateUser(@PathVariable @Positive Long userId, @Valid @RequestBody UserDto userDto) throws DuplicateEmailFoundException {
        return userServiceImpl.updateUser(userId, User.toUser(userDto));
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userServiceImpl.deleteUser(userId);
    }

    @GetMapping("{userId}")
    public User getUser(@PathVariable @Positive Long userId) {
        return userServiceImpl.getUser(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

}
