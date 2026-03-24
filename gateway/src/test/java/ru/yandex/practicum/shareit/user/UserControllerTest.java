package ru.yandex.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.exception.ErrorHandler;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ErrorHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void create_shouldReturnOk() throws Exception {
        UserDto dto = validUserDto();

        given(userClient.create(any(UserDto.class)))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "name", "Max",
                        "email", "max@test.com"
                )));

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("max@test.com"));

        verify(userClient).create(any(UserDto.class));
    }

    @Test
    void create_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        UserDto dto = validUserDto();
        dto.setEmail("not-an-email");

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("New name");

        given(userClient.update(eq(1L), any(UserDto.class)))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "name", "New name",
                        "email", "max@test.com"
                )));

        mockMvc.perform(patch("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"));

        verify(userClient).update(eq(1L), any(UserDto.class));
    }

    @Test
    void findAll_shouldReturnOk() throws Exception {
        given(userClient.findAll())
                .willReturn(ResponseEntity.ok(new Object[]{
                        Map.of("id", 1L, "name", "Max", "email", "max@test.com")
                }));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).findAll();
    }

    @Test
    void delete_shouldReturnOk() throws Exception {
        given(userClient.delete(1L)).willReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).delete(1L);
    }

    @Test
    void create_withBlankName_shouldReturnBadRequest() throws Exception {
        UserDto dto = validUserDto();
        dto.setName(" ");

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Validation error")));
    }

    private UserDto validUserDto() {
        UserDto dto = new UserDto();
        dto.setName("Max");
        dto.setEmail("max@test.com");
        return dto;
    }
}