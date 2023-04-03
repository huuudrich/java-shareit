package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.DuplicateEmailFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws DuplicateEmailFoundException {
        return userService.createUser(user);
    }

    @PatchMapping("{userId}")
    public User updateUser(@PathVariable @Positive Long userId, @Valid @RequestBody UserDto userDto) throws DuplicateEmailFoundException {
        return userService.updateUser(userId, User.toUser(userDto));
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("{userId}")
    public User getUser(@PathVariable @Positive Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @ExceptionHandler
    public ResponseEntity<String> handleCustomEmailException(DuplicateEmailFoundException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleCustomValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleCustomNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}
