package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {
    private Long id;

    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

}
