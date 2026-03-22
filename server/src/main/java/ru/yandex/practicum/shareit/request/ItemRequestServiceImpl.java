package ru.yandex.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareit.exception.NotFoundException;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.item.ItemJpaRepository;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestJpaRepository requestRepository;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        User requestor = getUserOrThrow(userId);

        ItemRequest request = ItemRequestMapper.toItemRequest(dto);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now().withNano(0));

        ItemRequest saved = requestRepository.save(request);
        return ItemRequestMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findAllForRequestor(Long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return toDtosWithItems(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findAllOtherUsers(Long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        return toDtosWithItems(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto findById(Long userId, Long requestId) {
        getUserOrThrow(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Такого запроса нет!"));

        ItemRequestDto dto = ItemRequestMapper.toDto(request);
        dto.setItems(getItemsForRequest(requestId));
        return dto;
    }

    private List<ItemRequestDto> toDtosWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<ItemForRequestDto>> itemsByRequestId = getItemsByRequestIds(requestIds);

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.toDto(request);
                    dto.setItems(itemsByRequestId.getOrDefault(request.getId(), List.of()));
                    return dto;
                })
                .toList();
    }

    private Map<Long, List<ItemForRequestDto>> getItemsByRequestIds(List<Long> requestIds) {
        Map<Long, List<ItemForRequestDto>> itemsByRequestId = new HashMap<>();

        List<Item> items = itemRepository.findAllByRequestIdInOrderByIdAsc(requestIds);

        for (Item item : items) {
            Long requestId = item.getRequest().getId();
            itemsByRequestId
                    .computeIfAbsent(requestId, id -> new ArrayList<>())
                    .add(ItemRequestMapper.toItemForRequestDto(item));
        }

        return itemsByRequestId;
    }

    private List<ItemForRequestDto> getItemsForRequest(Long requestId) {
        return itemRepository.findAllByRequestIdOrderByIdAsc(requestId).stream()
                .map(ItemRequestMapper::toItemForRequestDto)
                .toList();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет!"));
    }
}