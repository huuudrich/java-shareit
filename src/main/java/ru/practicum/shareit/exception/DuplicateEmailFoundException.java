package ru.practicum.shareit.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateEmailFoundException extends DataIntegrityViolationException {
    public DuplicateEmailFoundException(String message) {
        super(message);
    }
}
