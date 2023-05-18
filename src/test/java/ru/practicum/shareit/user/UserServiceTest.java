package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


public class UserServiceTest {
    private UserServiceImpl userService;
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("Test User");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setName("Test User");

        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto result = userService.createUser(user);

        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getName(), result.getName());
    }

    @Test
    public void updateUser() {
        User userOld = new User();
        userOld.setId(1L);
        userOld.setEmail("test@test.com");
        userOld.setName("Test User");

        User userNew = new User();
        userNew.setId(1L);
        userNew.setEmail("test@test.com");
        userNew.setName("Test User");

        Mockito.when(userRepository.existsById(userOld.getId())).thenReturn(true);

        Mockito.when(userRepository.findById(userOld.getId())).thenReturn(Optional.of(userOld));

        Mockito.when(userRepository.save(any(User.class))).thenReturn(userNew);

        UserDto result = userService.updateUser(userOld.getId(), userNew);

        assertEquals(userNew.getEmail(), result.getEmail());
        assertEquals(userNew.getName(), result.getName());
    }

    @Test
    public void when_UpdateNewUser_Email_isNull() {
        User userOld = new User();
        userOld.setId(1L);
        userOld.setEmail("old@test.com");
        userOld.setName("old Test User");

        User userNew = new User();
        userNew.setId(1L);
        userNew.setEmail(null);
        userNew.setName("new Test User");

        Mockito.when(userRepository.existsById(userOld.getId())).thenReturn(true);
        Mockito.when(userRepository.findById(userOld.getId())).thenReturn(Optional.of(userOld));

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            if (savedUser.getEmail() != null) {
                userOld.setEmail(savedUser.getEmail());
            }
            if (savedUser.getName() != null) {
                userOld.setName(savedUser.getName());
            }
            return userOld;
        });

        UserDto result = userService.updateUser(userOld.getId(), userNew);

        assertEquals(userOld.getEmail(), result.getEmail());
    }

    @Test
    public void when_UpdateNewUser_Name_isNull() {
        User userOld = new User();
        userOld.setId(1L);
        userOld.setEmail("old@test.com");
        userOld.setName("old Test User");

        User userNew = new User();
        userNew.setId(1L);
        userNew.setEmail("new@test.com");
        userNew.setName(null);

        Mockito.when(userRepository.existsById(userOld.getId())).thenReturn(true);
        Mockito.when(userRepository.findById(userOld.getId())).thenReturn(Optional.of(userOld));

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            if (savedUser.getEmail() != null) {
                userOld.setEmail(savedUser.getEmail());
            }
            if (savedUser.getName() != null) {
                userOld.setName(savedUser.getName());
            }
            return userOld;
        });

        UserDto result = userService.updateUser(userOld.getId(), userNew);

        assertEquals(userOld.getName(), result.getName());
    }

    @Test
    public void when_UpdateNewUser_NameAndEmail_isNull() {
        User userOld = new User();
        userOld.setId(1L);
        userOld.setEmail("old@test.com");
        userOld.setName("old Test User");

        User userNew = new User();
        userNew.setEmail(null);
        userNew.setName(null);

        Mockito.when(userRepository.existsById(userOld.getId())).thenReturn(true);
        Mockito.when(userRepository.findById(userOld.getId())).thenReturn(Optional.of(userOld));

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            if (savedUser.getEmail() != null) {
                userOld.setEmail(savedUser.getEmail());
            }
            if (savedUser.getName() != null) {
                userOld.setName(savedUser.getName());
            }
            return userOld;
        });

        UserDto result = userService.updateUser(userOld.getId(), userNew);

        assertEquals(userOld.getName(), result.getName());
        assertEquals(userOld.getEmail(), result.getEmail());
    }

    @Test
    public void deleteUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("Test User");

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Mockito.doNothing().when(userRepository).deleteById(user.getId());

        userService.deleteUser(user.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(user.getId());
    }

    @Test
    public void when_DeleteUser_IsWrongId() {
        Long correctUserId = 1L;
        Long wrongUserId = 2L;

        User user = new User();
        user.setId(correctUserId);
        user.setEmail("test@test.com");
        user.setName("Test User");

        Mockito.when(userRepository.findById(correctUserId)).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).deleteById(correctUserId);

        Mockito.doThrow(EntityNotFoundException.class).when(userRepository).deleteById(wrongUserId);

        userService.deleteUser(correctUserId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(correctUserId);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(wrongUserId));
    }

    @Test
    public void getUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("Test User");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setName("Test User");

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserDto result = userService.getUser(user.getId());

        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getName(), result.getName());
    }

    @Test
    public void when_getUser_IsWrongId() {
        Long correctUserId = 1L;
        Long wrongUserId = 2L;

        User user = new User();
        user.setId(correctUserId);
        user.setEmail("test@test.com");
        user.setName("Test User");

        Mockito.when(userRepository.findById(correctUserId)).thenReturn(Optional.of(user));

        Mockito.doThrow(EntityNotFoundException.class).when(userRepository).findById(wrongUserId);

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(wrongUserId));
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("test1@test.com");
        user1.setName("Test User 1");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@test.com");
        user2.setName("Test User 2");

        List<User> userList = Arrays.asList(user1, user2);

        Mockito.when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(userList.size(), result.size());
        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user1.getEmail(), result.get(0).getEmail());
        assertEquals(user1.getName(), result.get(0).getName());
        assertEquals(user2.getId(), result.get(1).getId());
        assertEquals(user2.getEmail(), result.get(1).getEmail());
        assertEquals(user2.getName(), result.get(1).getName());
    }

    @Test
    public void when_IsExistingUser_Exists() {
        Long existingUserId = 1L;

        Mockito.when(userRepository.existsById(existingUserId)).thenReturn(true);

        assertDoesNotThrow(() -> userService.isExistingUser(existingUserId));
    }

    @Test
    public void when_IsExistingUser_NotExists() {
        Long nonExistingUserId = 2L;

        Mockito.when(userRepository.existsById(nonExistingUserId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.isExistingUser(nonExistingUserId));
    }
}
