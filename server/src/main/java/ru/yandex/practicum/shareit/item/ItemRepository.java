package ru.yandex.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item create(Item item);
    Item update(Item newItem);
    void deleteById(Long id);
    Item findById(Long id);
    List<Item> findAllForUser(Long userId);
    List<Item> findAllByText(String text);
}
