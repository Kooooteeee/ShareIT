package ru.yandex.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareit.booking.Booking;
import ru.yandex.practicum.shareit.booking.BookingJpaRepository;
import ru.yandex.practicum.shareit.booking.Status;
import ru.yandex.practicum.shareit.exception.BadRequestException;
import ru.yandex.practicum.shareit.exception.NotFoundException;
import ru.yandex.practicum.shareit.exception.ValidationException;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;
    private final CommentJpaRepository commentRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет!"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        Item saved = itemRepository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(Long itemId, ItemDto patch, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет!"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи нет!"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем!");
        }

        if (patch.getName() != null) item.setName(patch.getName());
        if (patch.getDescription() != null) item.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) item.setAvailable(patch.getAvailable());

        Item saved = itemRepository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public void delete(Long itemId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет!"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи нет!"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем!");
        }

        itemRepository.delete(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto findById(Long itemId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет!"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи нет!"));

        ItemDto dto = ItemMapper.toItemDto(item);

        dto.setComments(getComments(itemId));

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            var now = LocalDateTime.now();

            bookingRepository
                    .findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(
                            itemId, Status.APPROVED, now
                    )
                    .ifPresent(b -> dto.setLastBooking(toShort(b)));

            bookingRepository
                    .findFirstByItemIdAndStatusAndStartGreaterThanOrderByStartAsc(
                            itemId, Status.APPROVED, now
                    )
                    .ifPresent(b -> dto.setNextBooking(toShort(b)));
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findAllForUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет!"));

        var now = LocalDateTime.now();

        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId).stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);

                    bookingRepository
                            .findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(
                                    item.getId(), Status.APPROVED, now
                            )
                            .ifPresent(b -> dto.setLastBooking(toShort(b)));

                    bookingRepository
                            .findFirstByItemIdAndStatusAndStartGreaterThanOrderByStartAsc(
                                    item.getId(), Status.APPROVED, now
                            )
                            .ifPresent(b -> dto.setNextBooking(toShort(b)));

                    dto.setComments(getComments(item.getId())); // ← ДОБАВИЛИ

                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findAllByText(Long userId, String text) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет!"));

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchAvailableByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentCreateDto dto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет!"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи нет!"));

        boolean hasPastBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, Status.APPROVED, LocalDateTime.now()
        );

        if (!hasPastBooking) {
            throw new ValidationException("Нельзя оставить отзыв: пользователь не брал вещь в аренду или аренда ещё не завершилась.");
        }

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDto(saved);
    }

    private ItemDto.BookingShort toShort(Booking b) {
        ItemDto.BookingShort s = new ItemDto.BookingShort();
        s.setId(b.getId());
        s.setBookerId(b.getBooker().getId());
        return s;
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findAllByItemIdOrderByCreatedDesc(itemId).stream()
                .map(CommentMapper::toDto)
                .toList();
    }
}