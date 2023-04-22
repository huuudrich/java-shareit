package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public Booking createBooking(@Valid @RequestBody BookingDto bookingDto, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public Booking setStatusByOwner(@PathVariable @Positive Long bookingId,
                                    @Positive @RequestHeader(xSharerUserId) Long userId,
                                    @RequestParam("approved") Boolean status) throws AccessDeniedException {
        return bookingService.setStatusForBookingByOwner(bookingId, userId, status);
    }

    @GetMapping("{bookingId}")
    public Booking getBookingWithOwnerOrBooker(@PathVariable @Positive Long bookingId,
                                               @Positive @RequestHeader(xSharerUserId) Long bookerIdOrOwnerId) {
        return bookingService.getBookingByIdForUserOrOwner(bookingId, bookerIdOrOwnerId);
    }

    @GetMapping
    public List<Booking> getAllBookingsWithBooker(@Positive @RequestHeader(xSharerUserId) Long bookerId,
                                                  @RequestParam(name = "state", required = false) BookingState bookingState) {
        return bookingService.getAllBookingsByBooker(bookerId, bookingState);
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingsWithOwner(@Positive @RequestHeader(xSharerUserId) Long ownerId,
                                                 @RequestParam(name = "state", required = false) BookingState bookingState) {
        return bookingService.getAllBookingsByOwner(ownerId, bookingState);
    }
}
