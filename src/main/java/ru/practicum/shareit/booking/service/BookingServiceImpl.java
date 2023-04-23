package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    public Booking createBooking(BookingDto bookingDto, Long userId) throws AccessDeniedException {
        log.info("Adding booking with id: {} for user with id: {}", bookingDto.getId(), userId);

        Booking booking = bookingMapper.toBooking(bookingDto);

        Long itemId = bookingDto.getItemId();
        Item item = Item.toItem(itemService.getItem(itemId, userId));
        User user = User.toUser(userService.getUser(userId));
        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new AccessDeniedException("Cannot be created with the same id");
        }
        if (item.getAvailable()) {
            booking.setBooker(user);
            booking.setItem(item);
            booking.setStatus(StatusBooking.WAITING);
            return bookingRepository.save(booking);
        }
        throw new ItemNotAvailableException(String.format("Status item id %d is false", itemId));
    }

    @Override
    public Booking setStatusForBookingByOwner(Long bookingId, Long userId, Boolean status) throws AccessDeniedException {
        log.info("Setting status for booking with id: {} by user with id: {}", bookingId, userId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (booking.getStatus().equals(StatusBooking.APPROVED)) {
            throw new ItemNotAvailableException("Status already: APPROVED");
        }
        User user = User.toUser(userService.getUser(userId));
        Long userIdWithItem = booking.getItem().getOwner().getId();
        if (Objects.equals(userIdWithItem, user.getId()) ||
                Objects.equals(userIdWithItem, booking.getBooker().getId())) {
            if (status) {
                booking.setStatus(StatusBooking.APPROVED);
            }
            if (!status) {
                booking.setStatus(StatusBooking.REJECTED);
            }
            return bookingRepository.save(booking);
        } else {
            throw new AccessDeniedException("Only the item owner can approve or reject a booking");
        }
    }

    @Override
    public Booking getBookingByIdForUserOrOwner(Long bookingId, Long bookerIdOrOwnerId) {
        log.info("Getting booking with id: {} for user or owner with id: {}", bookingId, bookerIdOrOwnerId);
        return bookingRepository.findByIdAndBookerIdOrOwnerId(bookingId, bookerIdOrOwnerId)
                .orElseThrow(() -> new EntityNotFoundException("Booking with id " + bookingId + " not found for user with id " + bookerIdOrOwnerId));
    }

    @Override
    public List<Booking> getAllBookingsByBooker(Long userId, BookingState state) {
        log.info("Getting all bookings by booker with id: {} and state: {}", userId, state);
        userService.getUser(userId);
        if (state == null) {
            state = BookingState.ALL;
        }
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllBookingsByBookerWithStateCurrent(userId);
            case PAST:
                return bookingRepository.findAllBookingsByBookerWithStatePast(userId);
            case FUTURE:
                return bookingRepository.findAllBookingsByBookerWithStateFuture(userId);
            case WAITING:
                return bookingRepository.findAllByBookerAndStatusIsWaitingOrderByStartDesc(userId);
            case REJECTED:
                return bookingRepository.findAllByBookerAndStatusIsRejectedOrderByStartDesc(userId);
            default:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        }
    }

    @Override
    public List<Booking> getAllBookingsByOwner(Long userId, BookingState state) {
        log.info("Getting all bookings by owner with id: {} and state: {}", userId, state);
        userService.getUser(userId);
        if (state == null) {
            state = BookingState.ALL;
        }
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllBookingsByOwnerWithStateCurrent(userId);
            case PAST:
                return bookingRepository.findAllBookingsByOwnerWithStatePast(userId);
            case FUTURE:
                return bookingRepository.findAllBookingsByOwnerWithStateFuture(userId);
            case WAITING:
                return bookingRepository.findAllByOwnerAndStatusIsWaitingOrderByStartDesc(userId);
            case REJECTED:
                return bookingRepository.findAllByOwnerAndStatusIsRejectedOrderByStartDesc(userId);
            default:
                return bookingRepository.findAllByOwnerIdOrderByStartDesc(userId);
        }
    }
}
