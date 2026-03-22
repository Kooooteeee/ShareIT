package ru.yandex.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);
    UserDto update(UserDto user, Long userId);
    UserDto findById(Long userId);
    void delete(Long userId);
    List<UserDto> findAll();
}
