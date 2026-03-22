package ru.yandex.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestCreateDto dto);

    List<ItemRequestDto> findAllForRequestor(Long userId);

    List<ItemRequestDto> findAllOtherUsers(Long userId);

    ItemRequestDto findById(Long userId, Long requestId);
}