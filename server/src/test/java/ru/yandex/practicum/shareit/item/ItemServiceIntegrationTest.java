package ru.yandex.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareit.booking.Booking;
import ru.yandex.practicum.shareit.booking.BookingJpaRepository;
import ru.yandex.practicum.shareit.booking.Status;
import ru.yandex.practicum.shareit.request.ItemRequest;
import ru.yandex.practicum.shareit.request.ItemRequestJpaRepository;
import ru.yandex.practicum.shareit.user.User;
import ru.yandex.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:item-service-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:schema.sql",
        "spring.jpa.defer-datasource-initialization=true"
})
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemJpaRepository itemRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ItemRequestJpaRepository requestRepository;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private CommentJpaRepository commentRepository;

    @Test
    void create_withRequestId_shouldLinkItemToRequest() {
        User owner = saveUser("Owner", "owner@test.com");
        User requestor = saveUser("Requestor", "requestor@test.com");

        ItemRequest request = new ItemRequest();
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now().minusHours(2).withNano(0));
        ItemRequest savedRequest = requestRepository.save(request);

        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("Good drill");
        dto.setAvailable(true);
        dto.setRequestId(savedRequest.getId());

        ItemDto created = itemService.create(dto, owner.getId());

        assertNotNull(created.getId());
        assertEquals(savedRequest.getId(), created.getRequestId());

        Item savedItem = itemRepository.findById(created.getId()).orElseThrow();
        assertNotNull(savedItem.getRequest());
        assertEquals(savedRequest.getId(), savedItem.getRequest().getId());
    }

    @Test
    void addComment_afterPastApprovedBooking_shouldSaveComment() {
        User owner = saveUser("Owner", "owner@test.com");
        User booker = saveUser("Booker", "booker@test.com");

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(true);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(savedItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2).withNano(0));
        booking.setEnd(LocalDateTime.now().minusDays(1).withNano(0));
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great thing");

        CommentDto created = itemService.addComment(savedItem.getId(), booker.getId(), dto);

        assertNotNull(created.getId());
        assertEquals("Great thing", created.getText());
        assertEquals("Booker", created.getAuthorName());

        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(savedItem.getId());
        assertEquals(1, comments.size());
        assertEquals("Great thing", comments.get(0).getText());
    }

    private User saveUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }
}