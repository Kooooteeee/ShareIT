package ru.yandex.practicum.shareit.booking;

import ru.yandex.practicum.shareit.booking.BookingCreateDto;
import ru.yandex.practicum.shareit.booking.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingCreateDto dto);

    BookingDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getByBooker(Long userId, String state);

    List<BookingDto> getByOwner(Long ownerId, String state);
}