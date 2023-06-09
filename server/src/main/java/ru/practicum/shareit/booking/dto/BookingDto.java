package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.annotation.ValidBookingDtoDates;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.booking.utils.StatusBooking;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ValidBookingDtoDates
public class BookingDto {
    private Long id;

    @FutureOrPresent(message = "Start date must be in the future or present")
    @NotNull(message = "Start date cannot be null")
    private LocalDateTime start;

    @FutureOrPresent(message = "End date must be in the future or present")
    @NotNull(message = "End date cannot be null")
    private LocalDateTime end;

    private Long itemId;

    private User booker;

    private StatusBooking status;

    private BookingState state;
}
