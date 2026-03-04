package ru.yandex.practicum.shareit.item;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(itemId, itemDto, userId);
    }

    @GetMapping
    public List<ItemDto> findAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllForUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findAllByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam("text") String text) {
        return itemService.findAllByText(userId, text);
    }
}
