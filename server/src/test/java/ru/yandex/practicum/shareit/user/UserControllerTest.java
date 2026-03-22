package ru.yandex.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.exception.ConflictException;
import ru.yandex.practicum.shareit.exception.ErrorHandler;
import ru.yandex.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
    private UserService userService;

    @Test
    void create_shouldReturnOk() throws Exception {
        UserDto request = validUserDto();
        UserDto response = validUserDto();
        response.setId(1L);

        given(userService.create(any(UserDto.class))).willReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("max@test.com"));

        verify(userService).create(any(UserDto.class));
    }

    @Test
    void create_whenConflict_shouldReturnConflict() throws Exception {
        given(userService.create(any(UserDto.class)))
                .willThrow(new ConflictException("Пользователь с такой почтой уже существует!"));

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.Conflict")
                        .value("Пользователь с такой почтой уже существует!"));
    }

    @Test
    void findById_whenNotFound_shouldReturnNotFound() throws Exception {
        given(userService.findById(1L))
                .willThrow(new NotFoundException("Такого пользователя нет!"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$['Not found']").value("Такого пользователя нет!"));
    }

    @Test
    void findAll_shouldReturnOk() throws Exception {
        UserDto dto = validUserDto();
        dto.setId(1L);

        given(userService.findAll()).willReturn(List.of(dto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        UserDto request = new UserDto();
        request.setName("New name");

        UserDto response = validUserDto();
        response.setId(1L);
        response.setName("New name");

        given(userService.update(any(UserDto.class), eq(1L))).willReturn(response);

        mockMvc.perform(patch("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"));

        verify(userService).update(any(UserDto.class), eq(1L));
    }

    @Test
    void delete_shouldReturnOk() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }

    private UserDto validUserDto() {
        UserDto dto = new UserDto();
        dto.setName("Max");
        dto.setEmail("max@test.com");
        return dto;
    }
}