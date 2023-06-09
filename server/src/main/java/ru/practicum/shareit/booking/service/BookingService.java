package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.BookingState;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface BookingService {
    Booking createBooking(BookingDto bookingDto, Long userId) throws AccessDeniedException;

    Booking setStatusForBookingByOwner(Long bookingId, Long userId, Boolean status) throws AccessDeniedException;

    Booking getBookingByIdForUserOrOwner(Long bookingId, Long bookerIdOrOwnerId);

    List<Booking> getAllBookings(Long userId, BookingState state, boolean isOwner, Pageable pageable);
}
