package ru.yandex.practicum.shareit.item;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(ErrorHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void create_shouldReturnOk() throws Exception {
        ItemDto dto = validItemDto();

        given(itemClient.create(eq(1L), any(ItemDto.class)))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "name", "Drill",
                        "description", "Good drill",
                        "available", true
                )));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(itemClient).create(eq(1L), any(ItemDto.class));
    }

    @Test
    void create_withBlankName_shouldReturnBadRequest() throws Exception {
        ItemDto dto = validItemDto();
        dto.setName(" ");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setName("Updated");

        given(itemClient.update(eq(1L), eq(2L), any(ItemDto.class)))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 2L,
                        "name", "Updated"
                )));

        mockMvc.perform(patch("/items/2")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(itemClient).update(eq(1L), eq(2L), any(ItemDto.class));
    }

    @Test
    void findAllForUser_shouldReturnOk() throws Exception {
        given(itemClient.findAllForUser(1L))
                .willReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1L, "name", "Drill")
                )));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemClient).findAllForUser(1L);
    }

    @Test
    void findById_shouldReturnOk() throws Exception {
        given(itemClient.findById(1L, 2L))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 2L,
                        "name", "Drill"
                )));

        mockMvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));

        verify(itemClient).findById(1L, 2L);
    }

    @Test
    void search_shouldReturnOk() throws Exception {
        given(itemClient.findAllByText(1L, "drill"))
                .willReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 2L, "name", "Drill")
                )));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));

        verify(itemClient).findAllByText(1L, "drill");
    }

    @Test
    void addComment_shouldReturnOk() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great");

        given(itemClient.addComment(eq(1L), eq(2L), any(CommentCreateDto.class)))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 10L,
                        "text", "Great"
                )));

        mockMvc.perform(post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great"));

        verify(itemClient).addComment(eq(1L), eq(2L), any(CommentCreateDto.class));
    }

    @Test
    void addComment_withBlankText_shouldReturnBadRequest() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText(" ");

        mockMvc.perform(post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    void findAllForUser_withoutHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("X-Sharer-User-Id")));
    }

    private ItemDto validItemDto() {
        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("Good drill");
        dto.setAvailable(true);
        return dto;
    }
}