package ru.yandex.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public Optional<User> update(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            return Optional.empty();
        }
        users.put(newUser.getId(), newUser);
        return Optional.of(newUser);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!users.containsKey(id)) {
            return false;
        }
        users.remove(id);
        return true;
    }

    @Override
    public Optional<User> findById(Long id) {
        if (!users.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(users.get(id));
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
