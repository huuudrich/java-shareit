package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.booking.utils.StatusBooking;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User booker;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private UserDto bookerDto;
    private BookingDto bookingDto;
    private Booking booking;

    AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);

        booker = new User();
        booker.setId(1L);
        booker.setEmail("booker@test.com");
        booker.setName("Test Booker");

        bookerDto = new UserDto();
        bookerDto.setId(1L);
        bookerDto.setEmail("booker@test.com");
        bookerDto.setName("Test Booker");

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

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setOwner(owner);
        itemDto.setName("Помидор");
        itemDto.setDescription("Помидор для поедания");
        itemDto.setRequest(null);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(StatusBooking.WAITING);
        booking.setEnd(LocalDateTime.now().plusHours(1L));
        booking.setStart(LocalDateTime.now());
        booking.setBooker(booker);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(item.getId());
        bookingDto.setStatus(StatusBooking.WAITING);
        bookingDto.setEnd(LocalDateTime.now().plusHours(1L));
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setBooker(booker);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void when_CreateBooking_OwnerId_And_BookerId_TheSameId() {
        owner.setId(1L);

        when(userService.getUser(anyLong())).thenReturn(bookerDto);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDto);

        assertThrows(AccessDeniedException.class, () -> bookingService.createBooking(bookingDto, owner.getId()));
    }

    @Test
    public void when_CreateBooking_Item_IsNotAvailable() {
        item.setAvailable(false);
        itemDto.setAvailable(false);

        when(userService.getUser(anyLong())).thenReturn(bookerDto);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);
        when(itemService.getItem(item.getId(), booker.getId())).thenReturn(itemDto);

        assertThrows(ItemNotAvailableException.class, () -> bookingService.createBooking(bookingDto, booker.getId()));
    }

    @Test
    public void setStatusForBookingByOwner_Success() throws AccessDeniedException {
        doNothing().when(userService).isExistingUser(any());
        when(bookingRepository.save(any())).thenReturn(booking);

        when(bookingRepository.getReferenceById(any())).thenReturn(booking);

        Booking result = bookingService.setStatusForBookingByOwner(booking.getId(), owner.getId(), true);

        assertEquals(StatusBooking.APPROVED, result.getStatus());
    }

    @Test
    public void when_SetStatusForBookingByOwner_Failure_NotOwner() {
        doNothing().when(userService).isExistingUser(any());

        when(bookingRepository.getReferenceById(any())).thenReturn(booking);

        assertThrows(AccessDeniedException.class, () -> bookingService.setStatusForBookingByOwner(booking.getId(), 3L, true));
    }

    @Test
    public void when_SetStatusForBookingByOwner_Failure_AlreadyApproved() {
        booking.setStatus(StatusBooking.APPROVED);

        doNothing().when(userService).isExistingUser(any());

        when(bookingRepository.getReferenceById(any())).thenReturn(booking);

        assertThrows(ItemNotAvailableException.class, () -> bookingService.setStatusForBookingByOwner(booking.getId(), owner.getId(), true));
    }

    @Test
    public void when_GetBookingByIdForUserOrOwner_Success() {
        when(bookingRepository.findByIdAndBookerIdOrOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingByIdForUserOrOwner(booking.getId(), owner.getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(booking.getBooker(), result.getBooker());
    }

    @Test
    public void when_GetBookingByIdForUserOrOwner_Failure_NotFound() {
        when(bookingRepository.findByIdAndBookerIdOrOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingByIdForUserOrOwner(1L, 1L));
    }

    @Test
    public void getAllBookings_CurrentState_Owner() {
        doNothing().when(userService).isExistingUser(any());
        when(bookingRepository.findAllByOwnerStateCurrent(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> result = bookingService.getAllBookings(1L, BookingState.CURRENT, true, Pageable.unpaged());

        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    public void getAllBookings_CurrentState_Booker() {
        doNothing().when(userService).isExistingUser(any());
        when(bookingRepository.findAllByBookerStateCurrent(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> result = bookingService.getAllBookings(1L, BookingState.CURRENT, false, Pageable.unpaged());

        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }
}
