package ru.practicum.shareit.item.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст не должен быть пустым")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
