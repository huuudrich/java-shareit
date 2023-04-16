package ru.practicum.shareit.exception;

public class DuplicateEmailFoundException extends Throwable {
    public DuplicateEmailFoundException(String message) {
        super(message);
    }
}
