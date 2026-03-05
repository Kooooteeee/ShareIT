package ru.yandex.practicum.shareit.user;

import java.util.Optional;

public interface UserRepository {
    User create(User user);
    User update(User newUser);
    void deleteById(Long id);
    User findById(Long id);
    boolean isUniqueEmail(String email, Long userId);
}
