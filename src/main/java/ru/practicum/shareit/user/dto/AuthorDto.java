package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Builder
public class AuthorDto {
    private Long id;

    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String authorName;

    public static AuthorDto toAuthorDto(User user) {
        return AuthorDto.builder()
                .id(user.getId())
                .authorName(user.getName()).build();
    }
}
