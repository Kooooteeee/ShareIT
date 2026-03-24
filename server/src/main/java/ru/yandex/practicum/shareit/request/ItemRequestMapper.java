package ru.yandex.practicum.shareit.request;

import ru.yandex.practicum.shareit.item.Item;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto dto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        return request;
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        ItemForRequestDto dto = new ItemForRequestDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }
}