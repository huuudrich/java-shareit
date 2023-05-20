package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(ItemRequestController.class)
public class ControllerRequestTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Long userId;
    private final static String baseUrl = "/requests";
    private final static String xSharerUserId = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {
        userId = 1L;
        itemRequest = new ItemRequest();
        itemRequestDto = new ItemRequestDto();

    }

    @Test
    public void createRequest_Success() throws Exception {
        itemRequest.setDescription("Full su");
        String inputJson = objectMapper.writeValueAsString(itemRequest);
        String outputJson = objectMapper.writeValueAsString(itemRequestDto);

        when(requestService.createRequest(any(ItemRequest.class), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, userId)
                .content(inputJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(outputJson));

        verify(requestService, times(1)).createRequest(any(ItemRequest.class), anyLong());
    }

    @Test
    public void getAllRequests_Success() throws Exception {
        List<ItemRequestDto> requests = Collections.singletonList(itemRequestDto);

        when(requestService.getAllRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get(baseUrl)
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        verify(requestService, times(1)).getAllRequests(anyLong());
    }

    @Test
    public void getRequest_Success() throws Exception {
        long requestId = 1L;

        when(requestService.getRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get(baseUrl + "/" + requestId)
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));

        verify(requestService, times(1)).getRequest(anyLong(), anyLong());
    }

    @Test
    public void getRequestsPagination_Success() throws Exception {
        int from = 0;
        int size = 10;
        List<ItemRequestDto> requests = Collections.singletonList(itemRequestDto);

        when(requestService.getAllRequestsPagination(anyLong(), any(Pageable.class))).thenReturn(requests);

        mockMvc.perform(get(baseUrl + "/all")
                        .header(xSharerUserId, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        verify(requestService, times(1)).getAllRequestsPagination(anyLong(), any(Pageable.class));
    }
}
