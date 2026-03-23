package ru.yandex.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;

    private BookingShort lastBooking;
    private BookingShort nextBooking;

    private List<CommentDto> comments;

    @Data
    public static class BookingShort {
        private Long id;
        private Long bookerId;
    }
}
