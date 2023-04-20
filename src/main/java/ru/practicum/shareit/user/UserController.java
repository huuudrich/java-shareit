package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public UserDto createUser(@Valid @RequestBody User user) {
        return userServiceImpl.createUser(user);
    }

    @PatchMapping("{userId}")
    public UserDto updateUser(@PathVariable @Positive Long userId, @Valid @RequestBody UserDto userDto){
        return userServiceImpl.updateUser(userId, User.toUser(userDto));
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userServiceImpl.deleteUser(userId);
    }

    @GetMapping("{userId}")
    public UserDto getUser(@PathVariable @Positive Long userId) {
        return userServiceImpl.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

}
