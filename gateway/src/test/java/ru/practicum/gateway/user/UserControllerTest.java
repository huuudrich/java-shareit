package ru.practicum.gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    private User user;

    private final static String API_PREFIX = "/users";

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("booker@test.com");
        user.setName("Test Booker");
    }

    @Test
    public void createUserTest() throws Exception {
        Mockito.when(userClient.createUser(Mockito.any())).thenReturn(new ResponseEntity<>(user, HttpStatus.CREATED));

        mockMvc.perform(MockMvcRequestBuilders
                        .post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated());

        Mockito.verify(userClient, Mockito.times(1)).createUser(Mockito.any());
    }

    @Test
    public void updateUserTest() throws Exception {
        Long userId = user.getId();

        Mockito.when(userClient.updateUser(Mockito.anyLong(), Mockito.any())).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mockMvc.perform(MockMvcRequestBuilders
                        .patch(API_PREFIX + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());

        Mockito.verify(userClient, Mockito.times(1)).updateUser(Mockito.anyLong(), Mockito.any());
    }

    @Test
    public void deleteUserTest() throws Exception {
        Long userId = user.getId();

        Mockito.doNothing().when(userClient).deleteUser(Mockito.anyLong());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(API_PREFIX + "/" + userId))
                .andExpect(status().isNoContent());

        Mockito.verify(userClient, Mockito.times(1)).deleteUser(Mockito.anyLong());
    }

    @Test
    public void getUserTest() throws Exception {
        Long userId = user.getId();

        Mockito.when(userClient.getUser(Mockito.anyLong())).thenReturn(ResponseEntity.ok(user));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_PREFIX + "/" + userId))
                .andExpect(status().isOk());

        Mockito.verify(userClient, Mockito.times(1)).getUser(Mockito.anyLong());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        Mockito.when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok(user));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_PREFIX))
                .andExpect(status().isOk());

        Mockito.verify(userClient, Mockito.times(1)).getAllUsers();
    }
}