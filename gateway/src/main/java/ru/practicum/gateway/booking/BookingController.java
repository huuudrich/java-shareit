package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody Object bookingDto, @Positive @RequestHeader(xSharerUserId) Long userId) {
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> setStatusByOwner(@PathVariable @Positive Long bookingId,
                                                   @Positive @RequestHeader(xSharerUserId) Long userId,
                                                   @RequestParam("approved") Boolean status) {
        return bookingClient.setStatusForBookingByOwner(bookingId, userId, status);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBookingWithOwnerOrBooker(@PathVariable @Positive Long bookingId,
                                                              @Positive @RequestHeader(xSharerUserId) Long bookerIdOrOwnerId) {
        return bookingClient.getBookingByIdForUserOrOwner(bookingId, bookerIdOrOwnerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsWithBooker(@Positive @RequestHeader(xSharerUserId) Long bookerId,
                                                           @RequestParam(name = "state", required = false) String bookingState,
                                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingClient.getAllBookings(bookerId, bookingState, false, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsWithOwner(@Positive @RequestHeader(xSharerUserId) Long ownerId,
                                                          @RequestParam(name = "state", required = false) String bookingState,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingClient.getAllBookings(ownerId, bookingState, true, from, size);
    }
}
