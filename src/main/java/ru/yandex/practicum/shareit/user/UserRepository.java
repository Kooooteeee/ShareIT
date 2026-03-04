package ru.yandex.practicum.shareit.user;

import java.util.Optional;

public interface UserRepository {
    User create(User user);
    Optional<User> update(User newUser);
    boolean deleteById(Long id);
    Optional<User> findById(Long id);
    boolean isUniqueEmail(String email, Long userId);
}
