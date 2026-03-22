package ru.yandex.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static Booking toEntity(BookingCreateDto dto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingDto toDto(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setStart(b.getStart());
        dto.setEnd(b.getEnd());
        dto.setStatus(b.getStatus().name());

        BookerDto booker = new BookerDto();
        booker.setId(b.getBooker().getId());
        dto.setBooker(booker);

        ItemHelpDto item = new ItemHelpDto();
        item.setId(b.getItem().getId());
        item.setName(b.getItem().getName());
        dto.setItem(item);

        return dto;
    }

    private static BookerDto toBookerDto(User user) {
        BookerDto dto = new BookerDto();
        dto.setId(user.getId());
        return dto;
    }

    private static ItemHelpDto toItemDto(Item item) {
        ItemHelpDto dto = new ItemHelpDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        return dto;
    }
}
