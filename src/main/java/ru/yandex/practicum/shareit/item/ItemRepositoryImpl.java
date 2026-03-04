package ru.yandex.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
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
    public Optional<Item> update(Item newItem) {
        if (!items.containsKey(newItem.getId())) {
            return Optional.empty();
        }
        items.put(newItem.getId(), newItem);
        return Optional.of(newItem);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!items.containsKey(id)) {
            return false;
        }
        items.remove(id);
        return true;
    }

    @Override
    public Optional<Item> findById(Long id) {
        if (!items.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(items.get(id));
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


