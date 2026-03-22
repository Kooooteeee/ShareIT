package ru.yandex.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestCreateDto dto) {
        log.info("Creating item request {}, userId={}", dto, userId);
        return itemRequestClient.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting own requests, userId={}", userId);
        return itemRequestClient.findAllForRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting other users requests, userId={}", userId);
        return itemRequestClient.findAllOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long requestId) {
        log.info("Getting request {}, userId={}", requestId, userId);
        return itemRequestClient.findById(userId, requestId);
    }
}