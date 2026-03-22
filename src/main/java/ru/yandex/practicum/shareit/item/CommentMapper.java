package ru.yandex.practicum.shareit.item;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setAuthorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null);
        return dto;
    }
}