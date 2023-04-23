package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;


@Component
@AllArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;

    public Booking toBooking(BookingDto bookingDto) {
        Item item = itemRepository.getReferenceById(bookingDto.getItemId());
        return Booking.builder()
                .id(bookingDto.getId())
                .booker(bookingDto.getBooker())
                .item(item)
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .status(bookingDto.getStatus())
                .build();
    }

    public static ItemDetailsDto.BookingInfo bookingInItemDto(Booking booking) {
        if (booking == null) return null;

        return ItemDetailsDto.BookingInfo.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart().toLocalDateTime())
                .end(booking.getEnd().toLocalDateTime()).build();
    }
}
