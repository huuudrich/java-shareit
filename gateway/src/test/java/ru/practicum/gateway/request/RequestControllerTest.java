package ru.practicum.gateway.request;

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

@WebMvcTest(RequestController.class)
public class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestClient requestClient;
    private final String xSharerUserId = "X-Sharer-User-Id";
    private ItemRequest request;

    @BeforeEach
    public void setup() {
        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Помидор");
        request.setRequester(null);
    }

    @Test
    public void createRequestTest() throws Exception {
        Long userId = 1L;

        Mockito.when(requestClient.createRequest(Mockito.any(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(request));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/requests")
                        .header(xSharerUserId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated());

        Mockito.verify(requestClient, Mockito.times(1)).createRequest(Mockito.any(), Mockito.anyLong());
    }

    @Test
    public void getAllRequestsTest() throws Exception {
        Long userId = 1L;

        Mockito.when(requestClient.getAllRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(request));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/requests")
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk());

        Mockito.verify(requestClient, Mockito.times(1)).getAllRequests(Mockito.anyLong());
    }

    @Test
    public void getRequestTest() throws Exception {
        Long userId = 1L;
        long requestId = 1L;

        Mockito.when(requestClient.getRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(request));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/requests/" + requestId)
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk());

        Mockito.verify(requestClient, Mockito.times(1)).getRequest(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void getRequestsPaginationTest() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        Mockito.when(requestClient.getAllRequestsPagination(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ResponseEntity.ok(request));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/requests/all?from=" + from + "&size=" + size)
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk());

        Mockito.verify(requestClient, Mockito.times(1))
                .getAllRequestsPagination(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

}
