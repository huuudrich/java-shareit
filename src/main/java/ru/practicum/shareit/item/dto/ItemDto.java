package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    private Long id;

    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description should be at most 500 characters")
    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;
}
