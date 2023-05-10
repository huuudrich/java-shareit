package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.utils.UserMapper.toUserDto;

public class RequestServiceTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private RequestServiceImpl requestService;
    private User owner;
    private Item item;
    private ItemRequest request;
    AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);

        owner = new User();
        owner.setId(2L);
        owner.setEmail("owner@test.com");
        owner.setName("Test Owner");

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setName("Помидор");
        item.setDescription("Помидор для поедания");
        item.setRequest(null);

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Помидор");
        request.setRequester(null);
        request.setCreated(LocalDateTime.now());
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void createRequest_Success() {
        ItemRequest itemRequest = new ItemRequest();
        Long userId = owner.getId();

        when(userService.getUser(anyLong())).thenReturn(toUserDto(owner));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = requestService.createRequest(itemRequest, userId);

        verify(userService, times(1)).getUser(anyLong());
        verify(requestRepository, times(1)).save(any(ItemRequest.class));

        assertNotNull(itemRequestDto);
    }

    @Test
    public void getRequest_Success() {
        Long userId = owner.getId();
        Long requestId = request.getId();

        doNothing().when(userService).isExistingUser(anyLong());
        when(requestRepository.getReferenceById(anyLong())).thenReturn(request);
        when(itemRepository.findAllByRequest(any(ItemRequest.class))).thenReturn(Collections.singletonList(item));

        ItemRequestDto itemRequestDto = requestService.getRequest(userId, requestId);

        verify(userService, times(1)).isExistingUser(anyLong());
        verify(requestRepository, times(1)).getReferenceById(anyLong());
        verify(itemRepository, times(1)).findAllByRequest(any(ItemRequest.class));

        assertNotNull(itemRequestDto);
    }

    @Test
    public void getAllRequests_Success() {
        Long userId = owner.getId();

        when(userService.getUser(anyLong())).thenReturn(toUserDto(owner));
        when(requestRepository.getAllByRequesterOrderByCreatedDesc(any(User.class))).thenReturn(Collections.singletonList(request));
        when(itemRepository.findAllByRequest(any(ItemRequest.class))).thenReturn(Collections.singletonList(item));

        List<ItemRequestDto> itemRequestDtoList = requestService.getAllRequests(userId);

        verify(userService, times(1)).getUser(anyLong());
        verify(requestRepository, times(1)).getAllByRequesterOrderByCreatedDesc(any(User.class));
        verify(itemRepository, times(1)).findAllByRequest(any(ItemRequest.class));

        assertEquals(1, itemRequestDtoList.size());
    }

    @Test
    public void getAllRequestsPagination_Success() {
        Long userId = owner.getId();
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(userService).isExistingUser(anyLong());
        when(requestRepository.findAllWithoutOwner(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(request)));
        when(itemRepository.findAllByRequest(any(ItemRequest.class))).thenReturn(Collections.singletonList(item));

        List<ItemRequestDto> itemRequestDtoList = requestService.getAllRequestsPagination(userId, pageable);

        verify(userService, times(1)).isExistingUser(anyLong());
        verify(requestRepository, times(1)).findAllWithoutOwner(anyLong(), any(Pageable.class));
        verify(itemRepository, times(1)).findAllByRequest(any(ItemRequest.class));

        assertEquals(1, itemRequestDtoList.size());
    }
}
