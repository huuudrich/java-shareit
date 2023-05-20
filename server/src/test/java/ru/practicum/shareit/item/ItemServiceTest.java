package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.StatusBooking;
import ru.practicum.shareit.exceptions.NotValidCommentException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.item.utils.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.utils.ItemMapper.toItemWithRequest;

public class ItemServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User booker;
    private User owner;
    private UserDto ownerDto;
    private Item item;
    private Booking booking;
    private ItemRequest request;
    private Comment comment;

    AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);

        booker = new User();
        booker.setId(1L);
        booker.setEmail("booker@test.com");
        booker.setName("Test Booker");

        owner = new User();
        owner.setId(2L);
        owner.setEmail("owner@test.com");
        owner.setName("Test Owner");

        ownerDto = new UserDto();
        ownerDto.setId(2L);
        ownerDto.setEmail("owner@test.com");
        ownerDto.setName("Test Owner");

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setName("Помидор");
        item.setDescription("Помидор для поедания");
        item.setRequest(null);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(StatusBooking.WAITING);
        booking.setEnd(LocalDateTime.now().plusHours(1L));
        booking.setStart(LocalDateTime.now());
        booking.setBooker(booker);

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

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void createItem_Success() {
        Long userId = owner.getId();

        when(userService.getUser(userId)).thenReturn(ownerDto);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemWithRequest itemWithRequest = toItemWithRequest(item, null);

        Object result = itemService.addItem(itemWithRequest, userId);

        assertTrue(result instanceof ItemDto);
        verify(userService, times(1)).getUser(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void when_CreateItem_WithRequest_Success() {
        Long userId = owner.getId();

        when(userService.getUser(userId)).thenReturn(ownerDto);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));

        ItemWithRequest itemWithRequest = toItemWithRequest(item, request);

        Object result = itemService.addItem(itemWithRequest, userId);

        assertTrue(result instanceof ItemWithRequest);
        verify(userService, times(1)).getUser(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void addComment_Success() {
        Long userId = booker.getId();

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(bookingRepository.findAllByBookerAndItemId(anyLong(), anyLong())).thenReturn(bookings);
        when(userRepository.getReferenceById(userId)).thenReturn(booker);

        when(itemRepository.getReferenceById(item.getId())).thenReturn(item);

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentRequest commentRequest = new CommentRequest("Классный помидор");

        CommentDto result = itemService.addComment(item.getId(), booker.getId(), commentRequest);

        assertEquals(result.getText(), comment.getText());
    }

    @Test
    public void when_AddComment_With_Not_Booking() {
        Long userId = booker.getId();

        List<Booking> bookings = new ArrayList<>();

        when(bookingRepository.findAllByBookerAndItemId(anyLong(), anyLong())).thenReturn(bookings);
        when(userRepository.getReferenceById(userId)).thenReturn(booker);

        when(itemRepository.getReferenceById(item.getId())).thenReturn(item);

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentRequest commentRequest = new CommentRequest("Классный помидор");

        assertThrows(NotValidCommentException.class, () -> itemService.addComment(item.getId(), booker.getId(), commentRequest));
    }

    @Test
    public void updateItem_success() {
        Long itemId = 1L;
        Long userId = 1L;
        Item inputItem = new Item();
        inputItem.setName("Updated Item");
        inputItem.setDescription("Updated Description");
        inputItem.setAvailable(true);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Item");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(false);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName(inputItem.getName());
        updatedItem.setDescription(inputItem.getDescription());
        updatedItem.setAvailable(inputItem.getAvailable());

        ItemDto updatedItemDto = toItemDto(updatedItem);

        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(itemId, inputItem, userId);

        assertEquals(updatedItemDto, result);
        verify(itemRepository, times(1)).findByIdAndOwnerId(itemId, userId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void searchItem_success() {
        String searchText = "item";
        Pageable pageable = PageRequest.of(0, 10);
        Item item = new Item();
        item.setName("item1");
        List<Item> items = List.of(item);

        when(itemRepository.search(searchText, pageable)).thenReturn(new PageImpl<>(items));

        List<ItemDto> result = itemService.searchItem(searchText, pageable);

        assertEquals(1, result.size());
        assertEquals("item1", result.get(0).getName());
        verify(itemRepository, times(1)).search(searchText, pageable);
    }

    @Test
    public void searchItem_emptyText() {
        String searchText = " ";
        Pageable pageable = PageRequest.of(0, 10);

        List<ItemDto> result = itemService.searchItem(searchText, pageable);

        assertTrue(result.isEmpty());
        verify(itemRepository, times(0)).search(anyString(), any(Pageable.class));
    }

    @Test
    void getItem_UserExists_ItemExists_ReturnsItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        Long itemId = item.getId();
        Long userId = owner.getId();
        when(userRepository.existsById(anyLong())).thenReturn(true);

        ItemDto result = itemService.getItem(itemId, userId);

        assertEquals(itemId, result.getId());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getItem_UserDoesNotExist_ThrowsEntityNotFoundException() {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();

        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(itemId, userId));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(0)).findById(anyLong());
    }

    @Test
    void getAllItemsWithUser_UserExists_ItemsExist_ReturnsItems() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = item.getOwner().getId();
        List<Item> items = Collections.singletonList(item);

        when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong(), any())).thenReturn(new PageImpl<>(items));
        when(commentRepository.findAllByItem_Id(anyLong())).thenReturn(new ArrayList<>());

        List<ItemDetailsDto> result = itemService.getAllItemsWithUser(userId, pageable);

        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findByOwnerIdOrderByIdAsc(anyLong(), any());
        verify(commentRepository, times(1)).findAllByItem_Id(anyLong());
    }

    @Test
    void getItemDetails_ItemExists_UserExists_ReturnsItemDetails() {
        Long itemId = 1L;
        Long userId = 1L;

        Item item = new Item();
        item.setId(itemId);
        User user = new User();
        user.setId(userId);
        List<Comment> comments = new ArrayList<>();

        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(commentRepository.findAllByItem_Id(itemId)).thenReturn(comments);

        Object result = itemService.getItemDetails(itemId, userId);

        assertNotNull(result);
        verify(itemRepository, times(1)).getReferenceById(itemId);
        verify(userRepository, times(1)).getReferenceById(userId);
        verify(commentRepository, times(1)).findAllByItem_Id(itemId);
    }
}

