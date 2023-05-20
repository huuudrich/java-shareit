package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.booking.utils.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;
    private User booker;
    private BookingDto bookingDto;
    private Booking booking;

    private final String baseUrl = "/bookings";
    private final String xSharerUserId = "X-Sharer-User-Id";

    @BeforeEach
    public void setup() {
        booker = new User();
        booker.setId(1L);
        booker.setEmail("booker@test.com");
        booker.setName("Test Booker");

        UserDto bookerDto = new UserDto();
        bookerDto.setId(1L);
        bookerDto.setEmail("booker@test.com");
        bookerDto.setName("Test Booker");

        User owner = new User();
        owner.setId(2L);
        owner.setEmail("owner@test.com");
        owner.setName("Test Owner");

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setName("Помидор");
        item.setDescription("Помидор для поедания");
        item.setRequest(null);

        ItemDto itemDto = new ItemDto();
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
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setBooker(booker);
    }

    @Test
    void when_TestCreateBooking_Data_IsNotValid() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));

        when(bookingService.createBooking(any(BookingDto.class), anyLong())).thenReturn(booking);

        this.mockMvc.perform(post(baseUrl)
                        .header(xSharerUserId, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void setStatusByOwner_Success() throws Exception {
        when(bookingService.setStatusForBookingByOwner(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        this.mockMvc.perform(patch(baseUrl + "/1")
                        .header(xSharerUserId, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingWithOwnerOrBooker_Success() throws Exception {
        when(bookingService.getBookingByIdForUserOrOwner(anyLong(), anyLong())).thenReturn(booking);

        this.mockMvc.perform(get(baseUrl + "/1")
                        .header(xSharerUserId, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsWithBooker_Success() throws Exception {
        List<Booking> bookings = new ArrayList<>();

        when(bookingService.getAllBookings(anyLong(), any(BookingState.class), anyBoolean(), any(Pageable.class))).thenReturn(bookings);

        this.mockMvc.perform(get(baseUrl)
                        .header(xSharerUserId, 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsWithOwner_Success() throws Exception {
        List<Booking> bookings = new ArrayList<>();

        when(bookingService.getAllBookings(anyLong(), any(BookingState.class), anyBoolean(), any(Pageable.class))).thenReturn(bookings);

        this.mockMvc.perform(get(baseUrl + "/owner")
                        .header(xSharerUserId, 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}
