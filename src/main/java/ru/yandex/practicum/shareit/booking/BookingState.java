package ru.yandex.practicum.shareit.booking;

import ru.yandex.practicum.shareit.exception.BadRequestException;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState from(String value) {
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Некорректное значение: " + value);
        }
    }
}