package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.booking.utils.StatusBooking;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static ru.practicum.shareit.booking.utils.BookingMapper.toBooking;
import static ru.practicum.shareit.item.utils.ItemMapper.toItem;
import static ru.practicum.shareit.user.utils.UserMapper.toUser;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public Booking createBooking(BookingDto bookingDto, Long bookerId) throws AccessDeniedException {
        log.info("Adding booking with id: {} for user with id: {}", bookingDto.getId(), bookerId);

        User booker = toUser(userService.getUser(bookerId));

        Item itemModelRepository = itemRepository.getReferenceById(bookingDto.getItemId());

        Booking booking = toBooking(bookingDto, itemModelRepository);

        Long itemId = bookingDto.getItemId();
        Item item = toItem(itemService.getItem(itemId, bookerId));
        Long ownerId = item.getOwner().getId();

        if (Objects.equals(ownerId, bookerId)) {
            throw new AccessDeniedException("Cannot be created with the same id");
        }
        if (item.getAvailable()) {
            booking.setBooker(booker);
            booking.setItem(item);
            booking.setStatus(StatusBooking.WAITING);
            return bookingRepository.save(booking);
        }
        throw new ItemNotAvailableException(format("Status item id %d is false", itemId));
    }

    public Booking setStatusForBookingByOwner(Long bookingId, Long userId, Boolean status) throws AccessDeniedException {
        log.info("Setting status for booking with id: {} by user with id: {}", bookingId, userId);

        userService.isExistingUser(userId);

        Booking booking = bookingRepository.getReferenceById(bookingId);
        Long bookerId = booking.getBooker().getId();
        Long userIdWithItem = booking.getItem().getOwner().getId();

        if (booking.getStatus().equals(StatusBooking.APPROVED)) {
            throw new ItemNotAvailableException("Status already: APPROVED");
        }

        if (Objects.equals(userIdWithItem, userId) || Objects.equals(userIdWithItem, bookerId)) {
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

    public Booking getBookingByIdForUserOrOwner(Long bookingId, Long bookerIdOrOwnerId) {
        log.info("Getting booking with id: {} for user or owner with id: {}", bookingId, bookerIdOrOwnerId);
        String notFoundText = format("Booking with id %d not found for user with id %d ", bookingId, bookerIdOrOwnerId);
        return bookingRepository.findByIdAndBookerIdOrOwnerId(bookingId, bookerIdOrOwnerId)
                .orElseThrow(() -> new EntityNotFoundException(notFoundText));
    }

    public List<Booking> getAllBookings(Long userId, BookingState state, boolean isOwner, Pageable pageable) {
        log.info("Getting all bookings by {} with id: {} and state: {}", isOwner ? "owner" : "booker", userId, state);

        userService.isExistingUser(userId);

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
