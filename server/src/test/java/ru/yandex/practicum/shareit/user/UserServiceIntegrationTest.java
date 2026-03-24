package ru.yandex.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:user-service-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:schema.sql",
        "spring.jpa.defer-datasource-initialization=true"
})
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaRepository userRepository;

    @Test
    void create_shouldPersistUser() {
        UserDto dto = new UserDto();
        dto.setName("Max");
        dto.setEmail("max@test.com");

        UserDto created = userService.create(dto);

        assertNotNull(created.getId());
        assertEquals("Max", created.getName());
        assertEquals("max@test.com", created.getEmail());

        User saved = userRepository.findById(created.getId()).orElseThrow();
        assertEquals("Max", saved.getName());
        assertEquals("max@test.com", saved.getEmail());
    }

    @Test
    void update_shouldChangeNameAndEmail() {
        User user = new User();
        user.setName("Old");
        user.setEmail("old@test.com");
        User saved = userRepository.save(user);

        UserDto patch = new UserDto();
        patch.setName("New");
        patch.setEmail("new@test.com");

        UserDto updated = userService.update(patch, saved.getId());

        assertEquals(saved.getId(), updated.getId());
        assertEquals("New", updated.getName());
        assertEquals("new@test.com", updated.getEmail());

        User reloaded = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("New", reloaded.getName());
        assertEquals("new@test.com", reloaded.getEmail());
    }

    @Test
    void findAll_shouldReturnCreatedUsers() {
        User first = new User();
        first.setName("Max");
        first.setEmail("max@test.com");

        User second = new User();
        second.setName("Ann");
        second.setEmail("ann@test.com");

        userRepository.save(first);
        userRepository.save(second);

        List<?> users = userService.findAll();

        assertEquals(2, users.size());
    }
}