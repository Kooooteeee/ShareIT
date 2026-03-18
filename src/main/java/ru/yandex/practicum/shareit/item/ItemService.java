package ru.yandex.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, Long userId);
    ItemDto update(Long itemId, ItemDto newItem, Long userId);
    void delete(Long itemId, Long userId);
    ItemDto findById(Long itemId, Long userId);
    List<ItemDto> findAllForUser(Long userId);
    List<ItemDto> findAllByText(Long userId, String text);
    CommentDto addComment(Long itemId, Long userId, CommentCreateDto dto);
}
