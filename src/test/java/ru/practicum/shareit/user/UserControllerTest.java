package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.Mockito.doThrow;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private final static String BasePATH = "/users";


    @Test
    public void createUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(BasePATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void createUser_whenUserEmailNotValid() throws Exception {
        User user = new User();
        user.setEmail("testexample.com");
        user.setName("Test User");

        UserDto userDto = new UserDto();
        userDto.setEmail("testexample.com");
        userDto.setName("Test User");

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(BasePATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createUser_whenUserNameEmptyNotValid() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("");

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(BasePATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createUser_whenDuplicateEmail_throwConstraintViolationException() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("");

        doThrow(ConstraintViolationException.class).when(userService).deleteUser(userId);

        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post(BasePATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        Mockito.when(userService.getUser(1L)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get(BasePATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void updateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Updated User");
        userDto.setEmail("updated@example.com");

        Mockito.when(userService.updateUser(Mockito.eq(1L), Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(BasePATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updated@example.com"));
    }
}
