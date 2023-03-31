package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import javax.validation.constraints.FutureOrPresent;

@Data
@Builder
public class Booking {
    private Long id;

    @FutureOrPresent(message = "Start date must be in the future or present")
    @NotNull(message = "Start date cannot be null")
    private ZonedDateTime start;

    @FutureOrPresent(message = "End date must be in the future or present")
    @NotNull(message = "End date cannot be null")
    private ZonedDateTime end;

    @NotNull(message = "Item cannot be null")
    private Item item;

    @NotNull(message = "Booker cannot be null")
    private User booker;

    private StatusBooking status;
}
