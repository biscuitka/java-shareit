package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private long id;
    private String name;
    @NotBlank(message = "Поле email обязательно для заполнения")
    @Email(regexp = "^([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+.[a-zA-Z]{2,})$", message = "Введен некорректный email")
    private String email;
}
