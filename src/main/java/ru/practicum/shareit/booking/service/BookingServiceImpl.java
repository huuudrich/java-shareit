package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public Booking createBooking(BookingDto bookingDto, Long userId) throws AccessDeniedException {
        log.info("Adding booking with id: {} for user with id: {}", bookingDto.getId(), userId);

        User user = User.toUser(userService.getUser(userId));

        Booking booking = bookingMapper.toBooking(bookingDto);

        Long itemId = bookingDto.getItemId();
        Item item = Item.toItem(itemService.getItem(itemId, userId));
        Long ownerId = item.getOwner().getId();

        if (Objects.equals(ownerId, userId)) {
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
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id %d not found", userId));
        }
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Long bookerId = booking.getBooker().getId();
        Long userIdWithItem = booking.getItem().getOwner().getId();

        if (booking.getStatus().equals(StatusBooking.APPROVED)) {
            throw new ItemNotAvailableException("Status already: APPROVED");
        }


        if (Objects.equals(userIdWithItem, userId) ||
                Objects.equals(userIdWithItem, bookerId)) {
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
    public List<Booking> getAllBookings(Long userId, BookingState state, boolean isOwner, Pageable pageable) {
        log.info("Getting all bookings by {} with id: {} and state: {}", isOwner ? "owner" : "booker", userId, state);
        userService.getUser(userId);
        LocalDateTime now = LocalDateTime.now();
        if (state == null) {
            state = BookingState.ALL;
        }
        switch (state) {
            case CURRENT:
                return isOwner ? bookingRepository.findAllByOwnerStateCurrent(userId, now, pageable).getContent()
                        : bookingRepository.findAllByBookerStateCurrent(userId, now, pageable).getContent();
            case PAST:
                return isOwner ? bookingRepository.findAllByItemOwnerIdAndEndBeforeAndStatusNotLike(userId, now, StatusBooking.REJECTED, pageable).getContent()
                        : bookingRepository.findAllByBookerIdAndEndIsBeforeAndStatusNotLike(userId, now, StatusBooking.REJECTED, pageable).getContent();
            case FUTURE:
                return isOwner ? bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, now, pageable).getContent()
                        : bookingRepository.findAllByBookerIdAndStartAfter(userId, now, pageable).getContent();
            case WAITING:
                return isOwner ? bookingRepository.findAllByItemOwnerIdAndStatusIsLike(userId, StatusBooking.WAITING, pageable).getContent()
                        : bookingRepository.findAllByBookerIdAndStatusIsLike(userId, StatusBooking.WAITING, pageable).getContent();
            case REJECTED:
                return isOwner ? bookingRepository.findAllByItemOwnerIdAndStatusIsLike(userId, StatusBooking.REJECTED, pageable).getContent()
                        : bookingRepository.findAllByBookerIdAndStatusIsLike(userId, StatusBooking.REJECTED, pageable).getContent();
            default:
                return isOwner ? bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageable).getContent()
                        : bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable).getContent();
        }
    }
}
