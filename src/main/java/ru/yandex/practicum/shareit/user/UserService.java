package ru.yandex.practicum.shareit.user;

public interface UserService {
    UserDto create(UserDto user);
    UserDto update(UserDto user, Long userId);
    UserDto findById(Long userId);
    void delete(Long userId);
}
