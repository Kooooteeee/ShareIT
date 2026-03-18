package ru.yandex.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareit.booking.BookingCreateDto;
import ru.yandex.practicum.shareit.booking.BookingDto;
import ru.yandex.practicum.shareit.exception.BadRequestException;
import ru.yandex.practicum.shareit.exception.ForbiddenException;
import ru.yandex.practicum.shareit.exception.NotFoundException;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.item.ItemJpaRepository;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingJpaRepository bookingRepository;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        User booker = getUserOrThrow(userId);
        Item item = getItemOrThrow(dto.getItemId());

        validateCreate(dto, item, booker);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(dto.getStart().withNano(0));
        booking.setEnd(dto.getEnd().withNano(0));
        booking.setStatus(Status.WAITING);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        ensureUserExistsOrForbidden(ownerId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено!"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Подтверждать бронирование может только владелец вещи!");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new BadRequestException("Бронирование уже обработано (не в статусе WAITING)!");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    private void ensureUserExistsOrForbidden(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ForbiddenException("Нет доступа.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long userId, Long bookingId) {
        getUserOrThrow(userId);

        Booking booking = bookingRepository.findAccessibleById(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено или нет доступа!"));

        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getByBooker(Long userId, String state) {
        getUserOrThrow(userId);

        BookingState bookingState = BookingState.from(state == null ? "ALL" : state);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository
                    .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };

        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getByOwner(Long ownerId, String state) {
        getUserOrThrow(ownerId);

        BookingState bookingState = BookingState.from(state == null ? "ALL" : state);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT -> bookingRepository
                    .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
        };

        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    // ----------------- помогалки(чтобы код не слишком раздувался) -----------------

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет!"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи нет!"));
    }

    private void validateCreate(BookingCreateDto dto, Item item, User booker) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new BadRequestException("Даты start и end обязательны!");
        }

        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new BadRequestException("Дата начала должна быть раньше даты окончания!");
        }

        LocalDateTime now = LocalDateTime.now();
        if (dto.getStart().isBefore(now) || dto.getEnd().isBefore(now)) {
            throw new BadRequestException("Нельзя бронировать в прошлом!");
        }

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования!");
        }

        if (item.getOwner().getId().equals(booker.getId())) {
            throw new NotFoundException("Нельзя бронировать собственную вещь!");
        }
    }
}