package ru.yandex.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.exception.ErrorHandler;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@Import(ErrorHandler.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void create_shouldReturnOk() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        given(itemRequestClient.create(eq(1L), any(ItemRequestCreateDto.class)))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "description", "Need a drill"
                )));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(itemRequestClient).create(eq(1L), any(ItemRequestCreateDto.class));
    }

    @Test
    void create_withBlankDescription_shouldReturnBadRequest() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription(" ");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    void findAllForRequestor_shouldReturnOk() throws Exception {
        given(itemRequestClient.findAllForRequestor(1L))
                .willReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1L, "description", "Need a drill")
                )));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemRequestClient).findAllForRequestor(1L);
    }

    @Test
    void findAllOtherUsers_shouldReturnOk() throws Exception {
        given(itemRequestClient.findAllOtherUsers(1L))
                .willReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 2L, "description", "Need a saw")
                )));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));

        verify(itemRequestClient).findAllOtherUsers(1L);
    }

    @Test
    void findById_shouldReturnOk() throws Exception {
        given(itemRequestClient.findById(1L, 2L))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 2L,
                        "description", "Need a saw"
                )));

        mockMvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));

        verify(itemRequestClient).findById(1L, 2L);
    }

    @Test
    void findById_withoutHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("X-Sharer-User-Id")));
    }
}