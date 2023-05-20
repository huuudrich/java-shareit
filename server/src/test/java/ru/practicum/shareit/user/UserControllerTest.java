package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private final String basePath = "/users";



    @Test
    public void createUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void when_createUser_Email_NotValid() throws Exception {
        User user = new User();
        user.setEmail("testexample.com");
        user.setName("Test User");

        UserDto userDto = new UserDto();
        userDto.setEmail("testexample.com");
        userDto.setName("Test User");

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void when_createUser_UserEmail_IsEmpty() throws Exception {
        User user = new User();
        user.setEmail("");
        user.setName("Test User");

        UserDto userDto = new UserDto();
        userDto.setEmail("");
        userDto.setName("Test User");

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void when_createUser_UserName_IsEmpty() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("");

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void when_createUser_UserEmail_IsDuplicate() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        doThrow(ConstraintViolationException.class).when(userService).createUser(user);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void getUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        Mockito.when(userService.getUser(1L)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void when_getUser_NonExisted() throws Exception {
        Long userId = 1L;

        doThrow(EntityNotFoundException.class).when(userService).getUser(userId);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Updated User");
        userDto.setEmail("updated@example.com");

        Mockito.when(userService.updateUser(Mockito.eq(1L), Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(basePath + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    public void deleteUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete(basePath + "/" + userId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    public void when_deleteUser_NonExisted() throws Exception {
        Long userId = 1L;

        doThrow(EntityNotFoundException.class).when(userService).deleteUser(userId);

        mockMvc.perform(MockMvcRequestBuilders.delete(basePath + "/" + userId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    public void getAllUsers() throws Exception {
        UserDto userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setName("Test User 1");
        userDto1.setEmail("test1@example.com");

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("Test User 2");
        userDto2.setEmail("test2@example.com");

        List<UserDto> userDtoList = Arrays.asList(userDto1, userDto2);

        Mockito.when(userService.getAllUsers()).thenReturn(userDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test User 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("test1@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Test User 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("test2@example.com"));
    }
}
