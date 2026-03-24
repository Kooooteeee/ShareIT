package ru.yandex.practicum.shareit.booking;

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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:booking-service-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:schema.sql",
        "spring.jpa.defer-datasource-initialization=true"
})
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ItemJpaRepository itemRepository;

    @Test
    void create_shouldSaveWaitingBooking() {
        User owner = saveUser("Owner", "owner@test.com");
        User booker = saveUser("Booker", "booker@test.com");
        Item item = saveItem(owner, true);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2).withNano(0));
        dto.setEnd(LocalDateTime.now().plusDays(1).withNano(0));

        BookingDto created = bookingService.create(booker.getId(), dto);

        assertNotNull(created.getId());
        assertEquals("WAITING", created.getStatus());

        Booking saved = bookingRepository.findById(created.getId()).orElseThrow();
        assertEquals(Status.WAITING, saved.getStatus());
        assertEquals(booker.getId(), saved.getBooker().getId());
        assertEquals(item.getId(), saved.getItem().getId());
    }

    @Test
    void approve_shouldChangeStatusToApproved() {
        User owner = saveUser("Owner", "owner@test.com");
        User booker = saveUser("Booker", "booker@test.com");
        Item item = saveItem(owner, true);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusHours(2).withNano(0));
        booking.setEnd(LocalDateTime.now().plusDays(1).withNano(0));
        booking.setStatus(Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        BookingDto approved = bookingService.approve(owner.getId(), savedBooking.getId(), true);

        assertEquals("APPROVED", approved.getStatus());

        Booking reloaded = bookingRepository.findById(savedBooking.getId()).orElseThrow();
        assertEquals(Status.APPROVED, reloaded.getStatus());
    }

    private User saveUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item saveItem(User owner, boolean available) {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }
}