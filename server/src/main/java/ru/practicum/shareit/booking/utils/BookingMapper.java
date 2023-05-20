package ru.practicum.shareit.booking.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.model.Item;


@Component
@AllArgsConstructor
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item) {
        return Booking.builder()
                .id(bookingDto.getId())
                .booker(bookingDto.getBooker())
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .item(item)
                .status(bookingDto.getStatus())
                .build();
    }

    public static ItemDetailsDto.BookingInfo bookingInItemDto(Booking booking) {
        if (booking == null) return null;

        return ItemDetailsDto.BookingInfo.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
