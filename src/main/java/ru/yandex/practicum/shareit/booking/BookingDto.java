package ru.yandex.practicum.shareit.booking;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private BookerDto booker;
    private ItemDto item;

    @Data
    public static class BookerDto { private Long id; }
    @Data
    public static class ItemDto { private Long id; private String name; }
}
