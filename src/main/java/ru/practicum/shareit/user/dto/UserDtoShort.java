package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@NoArgsConstructor
public class UserDtoShort {
    private Long id;

    public UserDtoShort(User user) {
        setId(user.getId());
    }
}
