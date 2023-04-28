package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class AuthorDto {
    private Long id;

    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String authorName;
}
