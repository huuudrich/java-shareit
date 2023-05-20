package ru.practicum.shareit.exceptions;

public class NotValidCommentException extends RuntimeException {
    public NotValidCommentException(String message) {
        super(message);
    }
}
