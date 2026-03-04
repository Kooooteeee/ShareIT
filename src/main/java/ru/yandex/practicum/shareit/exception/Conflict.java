package ru.yandex.practicum.shareit.exception;

public class Conflict extends RuntimeException {
    public Conflict(String message) {
        super(message);
    }
}
