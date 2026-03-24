package ru.yandex.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareit.item.Item;
import ru.yandex.practicum.shareit.item.ItemJpaRepository;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:request-service-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:schema.sql",
        "spring.jpa.defer-datasource-initialization=true"
})
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestJpaRepository requestRepository;

    @Autowired
    private ItemJpaRepository itemRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Test
    void create_shouldPersistRequest() {
        User requestor = saveUser("Requestor", "requestor@test.com");

        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        ItemRequestDto created = itemRequestService.create(requestor.getId(), dto);

        assertNotNull(created.getId());
        assertEquals("Need a drill", created.getDescription());
        assertNotNull(created.getCreated());

        ItemRequest saved = requestRepository.findById(created.getId()).orElseThrow();
        assertEquals("Need a drill", saved.getDescription());
        assertEquals(requestor.getId(), saved.getRequestor().getId());
    }

    @Test
    void findById_shouldReturnRequestWithItems() {
        User requestor = saveUser("Requestor", "requestor@test.com");
        User owner = saveUser("Owner", "owner@test.com");

        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now().minusHours(1).withNano(0));
        ItemRequest savedRequest = requestRepository.save(request);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(savedRequest);
        Item savedItem = itemRepository.save(item);

        ItemRequestDto result = itemRequestService.findById(owner.getId(), savedRequest.getId());

        assertEquals(savedRequest.getId(), result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(savedItem.getId(), result.getItems().get(0).getId());
        assertEquals("Drill", result.getItems().get(0).getName());
        assertEquals(owner.getId(), result.getItems().get(0).getOwnerId());
    }

    @Test
    void findAllForRequestor_shouldReturnOwnRequestsOrderedByCreatedDesc() {
        User requestor = saveUser("Requestor", "requestor@test.com");

        ItemRequest older = new ItemRequest();
        older.setDescription("Old request");
        older.setRequestor(requestor);
        older.setCreated(LocalDateTime.now().minusDays(1).withNano(0));
        requestRepository.save(older);

        ItemRequest newer = new ItemRequest();
        newer.setDescription("New request");
        newer.setRequestor(requestor);
        newer.setCreated(LocalDateTime.now().minusHours(1).withNano(0));
        requestRepository.save(newer);

        List<?> result = itemRequestService.findAllForRequestor(requestor.getId());

        assertEquals(2, result.size());
        ItemRequestDto first = (ItemRequestDto) result.get(0);
        ItemRequestDto second = (ItemRequestDto) result.get(1);

        assertEquals("New request", first.getDescription());
        assertEquals("Old request", second.getDescription());
    }

    private User saveUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }
}