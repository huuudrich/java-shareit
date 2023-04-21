package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
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
    public Booking createBooking(BookingDto bookingDto, Long userId) {
        log.info("Adding booking with id: {} for user with id: {}", bookingDto.getId(), userId);

        Booking booking = bookingMapper.toBooking(bookingDto);

        Long itemId = bookingDto.getItemId();
        Item item = Item.toItem(itemService.getItem(itemId, userId));
        User user = User.toUser(userService.getUser(userId));
        if (item != null && user != null && item.getAvailable()) {
            booking.setBooker(user);
            booking.setItem(item);
            booking.setStatus(StatusBooking.WAITING);
            return bookingRepository.save(booking);
        }
        throw new ItemNotAvailableException(String.format("Status item id %d is false", itemId));
    }

    @Override
    public Booking setStatusForBookingByOwner(Long bookingId, Long userId, Boolean status) throws AccessDeniedException {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        User user = User.toUser(userService.getUser(userId));
        Long userIdWithItem = booking.getItem().getOwner().getId();
        if (Objects.equals(userIdWithItem, user.getId())) {
            if (status = true) {
                booking.setStatus(StatusBooking.APPROVED);
            }
            if (status = false) {
                booking.setStatus(StatusBooking.REJECTED);
            }
            return bookingRepository.save(booking);
        } else {
            throw new AccessDeniedException("Only the item owner can approve or reject a booking");
        }
    }

    @Override
    public Booking getBookingByIdForUserOrOwner(Long bookingId, Long bookerIdOrOwnerId) {
        return bookingRepository.findByIdAndBookerIdOrOwnerId(bookingId, bookerIdOrOwnerId)
                .orElseThrow(() -> new EntityNotFoundException("Booking with id " + bookingId + " not found for user with id " + bookerIdOrOwnerId));
    }
}
