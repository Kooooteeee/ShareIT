package ru.yandex.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareit.exception.NotFoundException;
import ru.yandex.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto item, Long userId) {
        Item newItem = ItemMapper.toItem(item);
        newItem.setOwner(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Такого пользователя нет!")));
        return ItemMapper.toItemDto(itemRepository.create(newItem));
    }

    @Override
    public ItemDto update(Long itemId, ItemDto newItem, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        if (!itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Такой вещи нет!")).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем!");
        }
        Item item = itemRepository.findById(itemId).get();
        item.setName(newItem.getName() != null ? newItem.getName() : item.getName());
        item.setDescription(newItem.getDescription() != null ? newItem.getDescription() : item.getDescription());
        item.setAvailable(newItem.getAvailable() != null ? newItem.getAvailable() : item.getAvailable());
        return ItemMapper.toItemDto(itemRepository.update(item).get());
    }

    @Override
    public boolean delete(Long itemId, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        if (!itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Такой вещи нет!")).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем!");
        }
        return itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto findById(Long itemId, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Вещь не найдена!");
        }
        return ItemMapper.toItemDto(itemRepository.findById(itemId).get());
    }

    @Override
    public List<ItemDto> findAllForUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        return itemRepository.findAllForUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findAllByText(Long userId, String text) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет!");
        }
        return itemRepository.findAllByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
