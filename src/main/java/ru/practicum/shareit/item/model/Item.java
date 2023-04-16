package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class Item {
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description should be at most 500 characters")
    private String description;

    @NotNull(message = "available cannot be null")
    private Boolean available;

    private User owner;

    private ItemRequest request;

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest()).build();
    }
}
