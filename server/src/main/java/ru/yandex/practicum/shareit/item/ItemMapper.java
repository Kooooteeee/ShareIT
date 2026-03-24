package ru.yandex.practicum.shareit.item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null);
        dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);

        dto.setLastBooking(null);
        dto.setNextBooking(null);
        // dto.setComments(List.of());

        return dto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item newItem = new Item();
        newItem.setName(itemDto.getName());
        newItem.setDescription(itemDto.getDescription());
        newItem.setAvailable(itemDto.getAvailable());
        return newItem;
    }
}
