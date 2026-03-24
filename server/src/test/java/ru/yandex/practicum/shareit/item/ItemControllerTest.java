package ru.yandex.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.exception.ErrorHandler;
import ru.yandex.practicum.shareit.exception.NotFoundException;

import java.util.List;

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
    private ItemService itemService;

    @Test
    void create_shouldReturnOk() throws Exception {
        ItemDto request = validItemDto();
        ItemDto response = validItemDto();
        response.setId(1L);

        given(itemService.create(any(ItemDto.class), eq(1L))).willReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));

        verify(itemService).create(any(ItemDto.class), eq(1L));
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Updated");

        ItemDto response = validItemDto();
        response.setId(1L);
        response.setName("Updated");

        given(itemService.update(eq(1L), any(ItemDto.class), eq(2L))).willReturn(response);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(itemService).update(eq(1L), any(ItemDto.class), eq(2L));
    }

    @Test
    void findById_whenNotFound_shouldReturnNotFound() throws Exception {
        given(itemService.findById(1L, 2L))
                .willThrow(new NotFoundException("Такой вещи нет!"));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$['Not found']").value("Такой вещи нет!"));
    }

    @Test
    void findAllForUser_shouldReturnOk() throws Exception {
        ItemDto dto = validItemDto();
        dto.setId(1L);

        given(itemService.findAllForUser(2L)).willReturn(List.of(dto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void search_shouldReturnOk() throws Exception {
        ItemDto dto = validItemDto();
        dto.setId(1L);

        given(itemService.findAllByText(2L, "drill")).willReturn(List.of(dto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 2L)
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void addComment_shouldReturnOk() throws Exception {
        CommentCreateDto request = new CommentCreateDto();
        request.setText("Great");

        CommentDto response = new CommentDto();
        response.setId(10L);
        response.setText("Great");
        response.setAuthorName("Max");

        given(itemService.addComment(eq(1L), eq(2L), any(CommentCreateDto.class)))
                .willReturn(response);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great"));

        verify(itemService).addComment(eq(1L), eq(2L), any(CommentCreateDto.class));
    }

    private ItemDto validItemDto() {
        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("Good drill");
        dto.setAvailable(true);
        return dto;
    }
}