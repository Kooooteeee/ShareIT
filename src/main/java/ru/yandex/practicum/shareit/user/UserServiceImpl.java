package ru.yandex.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.exception.Conflict;
import ru.yandex.practicum.shareit.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto user) {
        if (!userRepository.isUniqueEmail(user.getEmail(), null)) {
            throw new Conflict("Пользователь с такой почтой уже существует!");
        }
        return UserMapper.toUserDto(userRepository.create(UserMapper.toUser(user)));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        if (userDto.getEmail() != null &&
                !userRepository.isUniqueEmail(userDto.getEmail(), userId)) {
            throw new Conflict("Пользователь с такой почтой уже существует!");
        }
        User user = userRepository.findById(userId).get();
        user.setName(userDto.getName() != null ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail());
        return UserMapper.toUserDto(userRepository.update(user).get());
    }

    @Override
    public UserDto findById(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        return UserMapper.toUserDto(userRepository.findById(userId).get());
    }

    @Override
    public void delete(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        userRepository.deleteById(userId);
    }
}
