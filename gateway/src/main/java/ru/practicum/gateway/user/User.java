package ru.practicum.gateway.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

}
