package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingDto bookingDto, @Positive @RequestHeader(xSharerUserId) Long userId) throws AccessDeniedException {
        Booking newBooking = bookingService.createBooking(bookingDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBooking);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Booking> setStatusByOwner(@PathVariable @Positive Long bookingId,
                                                    @Positive @RequestHeader(xSharerUserId) Long userId,
                                                    @RequestParam("approved") Boolean status) throws AccessDeniedException {
        Booking updatedBooking = bookingService.setStatusForBookingByOwner(bookingId, userId, status);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Booking> getBookingWithOwnerOrBooker(@PathVariable @Positive Long bookingId,
                                                               @Positive @RequestHeader(xSharerUserId) Long bookerIdOrOwnerId) {
        Booking booking = bookingService.getBookingByIdForUserOrOwner(bookingId, bookerIdOrOwnerId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookingsWithBooker(@Positive @RequestHeader(xSharerUserId) Long bookerId,
                                                                  @RequestParam(name = "state", required = false) BookingState bookingState,
                                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                  @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings = bookingService.getAllBookings(bookerId, bookingState, false, pageable);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<Booking>> getAllBookingsWithOwner(@Positive @RequestHeader(xSharerUserId) Long ownerId,
                                                                 @RequestParam(name = "state", required = false) BookingState bookingState,
                                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                 @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings = bookingService.getAllBookings(ownerId, bookingState, true, pageable);
        return ResponseEntity.ok(bookings);
    }
}
