package ru.yandex.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.shareit.exception.NotFoundException;

import java.util.*;

public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        Item newItem = item;
        newItem.setId(createNewId());
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public Item update(Item newItem) {
        if (!items.containsKey(newItem.getId())) {
            throw new NotFoundException("Такой вещи нет!");
        }
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public void deleteById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Такой вещи нет!");
        }
        items.remove(id);
    }

    @Override
    public Item findById(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Такой вещи нет!");
        }
        return items.get(id);
    }

    @Override
    public List<Item> findAllForUser(Long userId) {
        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .toList();
    }

    @Override
    public List<Item> findAllByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return items.values().stream().
                filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        i.getAvailable())
                .toList();
    }

    private Long createNewId() {
        return items.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}


