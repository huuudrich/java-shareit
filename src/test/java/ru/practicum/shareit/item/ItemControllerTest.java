package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.utils.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.utils.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.utils.ItemMapper.toItemWithRequest;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private Item item;
    private ItemDto itemDto;
    private User user;
    private User booker;
    private ItemRequest request;
    private Comment comment;

    @BeforeEach
    public void setup() {
        booker = new User();
        booker.setId(1L);
        booker.setEmail("booker@test.com");
        booker.setName("Test Booker");

        user = new User();
        user.setId(2L);
        user.setEmail("owner@test.com");
        user.setName("Test Owner");

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(user);
        item.setName("Помидор");
        item.setDescription("Помидор для поедания");
        item.setRequest(null);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setOwner(user);
        itemDto.setName("Помидор");
        itemDto.setDescription("Помидор для поедания");
        itemDto.setRequest(null);

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Помидор");
        request.setRequester(booker);
        request.setCreated(LocalDateTime.now());

        comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(booker);
        comment.setText("Классный помидор");
    }

    @Test
    public void addItem_Success() throws Exception {
        ItemWithRequest itemRequest = toItemWithRequest(item, request);
        Long userId = user.getId();

        String inputJson = objectMapper.writeValueAsString(itemRequest);
        String outputJson = objectMapper.writeValueAsString(itemDto);

        when(itemService.addItem(any(ItemWithRequest.class), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(inputJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(outputJson));

        verify(itemService, times(1)).addItem(any(ItemWithRequest.class), anyLong());
    }

    @Test
    public void updateItem_Success() throws Exception {
        Long itemId = item.getId();
        Long userId = user.getId();

        String inputJson = objectMapper.writeValueAsString(itemDto);
        String outputJson = objectMapper.writeValueAsString(itemDto);

        when(itemService.updateItem(anyLong(), any(Item.class), anyLong())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().json(outputJson));

        verify(itemService, times(1)).updateItem(anyLong(), any(Item.class), anyLong());
    }

    @Test
    public void getItem_Success() throws Exception {
        Long itemId = item.getId();
        Long userId = user.getId();
        List<CommentDto> commentDtoList = Collections.singletonList(toCommentDto(comment));

        ItemDetailsDto itemDetailsDto = new ItemDetailsDto();
        itemDetailsDto.setId(1L);
        itemDetailsDto.setName("Помидор");
        itemDetailsDto.setAvailable(true);
        itemDetailsDto.setComments(commentDtoList);
        itemDetailsDto.setNextBooking(new ItemDetailsDto.BookingInfo(1L,
                LocalDateTime.now().minusHours(1L),
                LocalDateTime.now().plusHours(1L), booker.getId()));
        itemDetailsDto.setLastBooking(new ItemDetailsDto.BookingInfo(1L,
                LocalDateTime.now().minusHours(1L),
                LocalDateTime.now().plusHours(1L), booker.getId()));

        String outputJson = objectMapper.writeValueAsString(itemDetailsDto);

        when(itemService.getItemDetails(anyLong(), anyLong())).thenReturn(itemDetailsDto);

        mockMvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(outputJson));

        verify(itemService, times(1)).getItemDetails(anyLong(), anyLong());
    }

    @Test
    public void getAllItemsWithUser_Success() throws Exception {
        Long userId = user.getId();
        List<CommentDto> commentDtoList = Collections.singletonList(toCommentDto(comment));

        ItemDetailsDto itemDetailsDto = new ItemDetailsDto();
        itemDetailsDto.setId(1L);
        itemDetailsDto.setName("Помидор");
        itemDetailsDto.setAvailable(true);
        itemDetailsDto.setComments(commentDtoList);
        itemDetailsDto.setNextBooking(new ItemDetailsDto.BookingInfo(1L,
                LocalDateTime.now().minusHours(1L),
                LocalDateTime.now().plusHours(1L), booker.getId()));
        itemDetailsDto.setLastBooking(new ItemDetailsDto.BookingInfo(1L,
                LocalDateTime.now().minusHours(1L),
                LocalDateTime.now().plusHours(1L), booker.getId()));

        List<ItemDetailsDto> itemDetailsDtoList = List.of(itemDetailsDto);

        String outputJson = objectMapper.writeValueAsString(itemDetailsDtoList);

        when(itemService.getAllItemsWithUser(anyLong(), any(Pageable.class))).thenReturn(itemDetailsDtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(outputJson));

        verify(itemService, times(1)).getAllItemsWithUser(anyLong(), any(Pageable.class));
    }

    @Test
    public void searchItem_Success() throws Exception {
        String text = "searchText";
        List<ItemDto> itemDtoList = Collections.singletonList(toItemDto(item));

        String outputJson = objectMapper.writeValueAsString(itemDtoList);

        when(itemService.searchItem(anyString(), any(Pageable.class))).thenReturn(itemDtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(outputJson));

        verify(itemService, times(1)).searchItem(anyString(), any(Pageable.class));
    }

    @Test
    public void getComments_Success() throws Exception {
        Long itemId = item.getId();
        Long userId = user.getId();
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("test comment");
        CommentDto commentDto = toCommentDto(comment);

        String inputJson = objectMapper.writeValueAsString(commentRequest);
        String outputJson = objectMapper.writeValueAsString(commentDto);

        when(itemService.addComment(anyLong(), anyLong(), any(CommentRequest.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().json(outputJson));

        verify(itemService, times(1)).addComment(anyLong(), anyLong(), any(CommentRequest.class));
    }
}
