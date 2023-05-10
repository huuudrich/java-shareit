package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.booking.utils.StatusBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @Transactional
    public void getAllBookings_CurrentState_Owner() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        userRepository.save(user);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStatus(StatusBooking.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(3));
        bookingRepository.save(booking);

        List<Booking> result = bookingService.getAllBookings(user.getId(), BookingState.FUTURE, false, Pageable.unpaged());

        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }
}
