package ru.yandex.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.shareit.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        User newUser = user;
        newUser.setId(createNewId());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User update(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void deleteById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        users.remove(id);
    }

    @Override
    public User findById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        return users.get(id);
    }

    @Override
    public boolean isUniqueEmail(String email, Long userId) {
        return users.values().stream().noneMatch(u -> u.getEmail().equals(email) &&
                (userId != null ? !u.getId().equals(userId) : true));
    }

    private Long createNewId() {
        return users.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
