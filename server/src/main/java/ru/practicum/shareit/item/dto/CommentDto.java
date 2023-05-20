package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;

    @NotBlank(message = "Text cannot be blank")
    @Size(min = 1, max = 100, message = "Text should be between 1 and 100 characters")
    private String text;

    private Item item;

    private String authorName;

    private LocalDateTime created;
}
