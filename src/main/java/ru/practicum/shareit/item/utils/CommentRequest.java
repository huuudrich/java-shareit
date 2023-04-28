package ru.practicum.shareit.item.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    @NotBlank(message = "Text cannot be blank")
    @Size(min = 1, max = 100, message = "Text should be between 1 and 100 characters")
    private String text;
}
