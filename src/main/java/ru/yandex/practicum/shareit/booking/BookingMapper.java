package ru.yandex.practicum.shareit.booking;

import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.user.User;

public final class BookingMapper {

    private BookingMapper() {
    }

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

        BookingDto.BookerDto booker = new BookingDto.BookerDto();
        booker.setId(b.getBooker().getId());
        dto.setBooker(booker);

        BookingDto.ItemDto item = new BookingDto.ItemDto();
        item.setId(b.getItem().getId());
        item.setName(b.getItem().getName());
        dto.setItem(item);

        return dto;
    }

    private static BookingDto.BookerDto toBookerDto(User user) {
        BookingDto.BookerDto dto = new BookingDto.BookerDto();
        dto.setId(user.getId());
        return dto;
    }

    private static BookingDto.ItemDto toItemDto(Item item) {
        BookingDto.ItemDto dto = new BookingDto.ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        return dto;
    }
}
