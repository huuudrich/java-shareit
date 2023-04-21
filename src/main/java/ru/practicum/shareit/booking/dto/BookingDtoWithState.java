package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.booking.annotation.ValidBookingDtoDates;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Data
@ValidBookingDtoDates
public class BookingDtoWithState {
    private Long id;

    @FutureOrPresent(message = "Start date must be in the future or present")
    @NotNull(message = "Start date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private ZonedDateTime start;

    @FutureOrPresent(message = "End date must be in the future or present")
    @NotNull(message = "End date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private ZonedDateTime end;

    private Long itemId;

    private User booker;

    private BookingState state;
}
