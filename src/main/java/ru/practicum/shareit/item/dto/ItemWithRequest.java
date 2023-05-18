package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithRequest {

    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description should be at most 500 characters")
    private String description;

    @NotNull(message = "available cannot be null")
    @Column(name = "is_available")
    private Boolean available;

    private User owner;

    private Long requestId;

}
