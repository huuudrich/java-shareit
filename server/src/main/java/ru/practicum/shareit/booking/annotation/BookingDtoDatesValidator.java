package ru.practicum.shareit.booking.annotation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingDtoDatesValidator implements ConstraintValidator<ValidBookingDtoDates, BookingDto> {

    @Override
    public void initialize(ValidBookingDtoDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            return true;
        }
        return bookingDto.getEnd().isAfter(bookingDto.getStart());
    }
}
