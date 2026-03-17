package ru.yandex.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.exception.ConflictException;
import ru.yandex.practicum.shareit.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto user) {
        if (!userRepository.isUniqueEmail(user.getEmail(), null)) {
            throw new ConflictException("Пользователь с такой почтой уже существует!");
        }
        return UserMapper.toUserDto(userRepository.create(UserMapper.toUser(user)));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        if (userDto.getEmail() != null &&
                !userRepository.isUniqueEmail(userDto.getEmail(), userId)) {
            throw new ConflictException("Пользователь с такой почтой уже существует!");
        }
        User user = userRepository.findById(userId);
        user.setName(userDto.getName() != null ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail());
        return UserMapper.toUserDto(userRepository.update(user));
    }

    @Override
    public UserDto findById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
