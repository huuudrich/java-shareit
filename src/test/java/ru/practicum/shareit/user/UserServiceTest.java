package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.utils.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class UserServiceTest {
    private UserServiceImpl userService;
    private UserRepository userRepository;
    private UserMapper userMapper;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userMapper = Mockito.mock(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    public void createUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setName("Test User");

        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        Mockito.when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.createUser(user);

        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getName(), result.getName());
    }
}
