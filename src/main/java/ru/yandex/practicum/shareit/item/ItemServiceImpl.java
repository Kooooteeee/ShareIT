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
        newItem.setOwner(userRepository.findById(userId));
        return ItemMapper.toItemDto(itemRepository.create(newItem));
    }

    @Override
    public ItemDto update(Long itemId, ItemDto newItem, Long userId) {
        userRepository.findById(userId);
        if (!itemRepository.findById(itemId).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем!");
        }
        Item item = itemRepository.findById(itemId);
        item.setName(newItem.getName() != null ? newItem.getName() : item.getName());
        item.setDescription(newItem.getDescription() != null ? newItem.getDescription() : item.getDescription());
        item.setAvailable(newItem.getAvailable() != null ? newItem.getAvailable() : item.getAvailable());
        return ItemMapper.toItemDto(itemRepository.update(item));
    }

    @Override
    public void delete(Long itemId, Long userId) {
        userRepository.findById(userId);
        if (!itemRepository.findById(itemId).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем!");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto findById(Long itemId, Long userId) {
        userRepository.findById(userId);
        return ItemMapper.toItemDto(itemRepository.findById(itemId));
    }

    @Override
    public List<ItemDto> findAllForUser(Long userId) {
        userRepository.findById(userId);
        return itemRepository.findAllForUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findAllByText(Long userId, String text) {
        userRepository.findById(userId);
        return itemRepository.findAllByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
