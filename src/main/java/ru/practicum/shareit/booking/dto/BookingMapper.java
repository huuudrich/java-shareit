package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import javax.persistence.EntityNotFoundException;

@Component
@AllArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;

    public Booking toBooking(BookingDto bookingDto) {
        Item item = itemRepository.getById(bookingDto.getItemId());
        if (item != null) {
            return Booking.builder()
                    .id(bookingDto.getId())
                    .booker(bookingDto.getBooker())
                    .item(item)
                    .end(bookingDto.getEnd())
                    .start(bookingDto.getStart())
                    .status(bookingDto.getStatus())
                    .build();
        } else {
            throw new EntityNotFoundException("Item with id " + item.getId() + " not found");
        }
    }
}
