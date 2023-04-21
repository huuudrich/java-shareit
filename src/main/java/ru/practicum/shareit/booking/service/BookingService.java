package ru.practicum.shareit.booking.service;

import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithState;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface BookingService {
    Booking createBooking(BookingDto bookingDto, Long userId);

    Booking setStatusForBookingByOwner(Long bookingId, Long userId, Boolean status) throws AccessDeniedException;

    Booking getBookingByIdForUserOrOwner(Long bookingId, Long bookerIdOrOwnerId);

    List<Booking> getAllBookingsByBooker(Long userId, BookingState state);
}
