package ru.yandex.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestCreateDto dto) {
        return itemRequestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllForRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAllForRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAllOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long requestId) {
        return itemRequestService.findById(userId, requestId);
    }
}