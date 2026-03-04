package ru.yandex.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.shareit.request.ItemRequest;
import ru.yandex.practicum.shareit.user.User;

@EqualsAndHashCode( of = "id")
@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
