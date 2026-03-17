package ru.yandex.practicum.shareit.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.shareit.user.User;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
