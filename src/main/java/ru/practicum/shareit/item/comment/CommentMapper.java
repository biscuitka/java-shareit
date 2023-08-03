package ru.practicum.shareit.item.comment;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment fromDtoToComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public static CommentDto fromCommentToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public static List<CommentDto> fromListOfCommentToDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::fromCommentToDto)
                .collect(Collectors.toList());
    }
}
