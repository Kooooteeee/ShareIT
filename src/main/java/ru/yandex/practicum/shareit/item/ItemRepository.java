package ru.yandex.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);
    Optional<Item> update(Item newItem);
    boolean deleteById(Long id);
    Optional<Item> findById(Long id);
    List<Item> findAllForUser(Long userId);
    List<Item> findAllByText(String text);
}
