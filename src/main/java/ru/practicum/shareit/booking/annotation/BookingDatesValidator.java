package ru.practicum.shareit.booking.annotation;

import ru.practicum.shareit.booking.model.Booking;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, Booking> {

    @Override
    public void initialize(ValidBookingDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext context) {
        if (booking.getStart() == null || booking.getEnd() == null) {
            return true;
        }
        return booking.getEnd().isAfter(booking.getStart());
    }
}
