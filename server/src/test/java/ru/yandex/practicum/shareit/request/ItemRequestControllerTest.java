package ru.yandex.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.exception.ErrorHandler;
import ru.yandex.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

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
    private ItemRequestService itemRequestService;

    @Test
    void create_shouldReturnOk() throws Exception {
        ItemRequestCreateDto request = new ItemRequestCreateDto();
        request.setDescription("Need a drill");

        ItemRequestDto response = new ItemRequestDto();
        response.setId(1L);
        response.setDescription("Need a drill");
        response.setCreated(LocalDateTime.now());

        given(itemRequestService.create(eq(1L), any(ItemRequestCreateDto.class))).willReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));

        verify(itemRequestService).create(eq(1L), any(ItemRequestCreateDto.class));
    }

    @Test
    void findAllForRequestor_shouldReturnOk() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need a drill");
        dto.setCreated(LocalDateTime.now());

        given(itemRequestService.findAllForRequestor(1L)).willReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemRequestService).findAllForRequestor(1L);
    }

    @Test
    void findAllOtherUsers_shouldReturnOk() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(2L);
        dto.setDescription("Need a saw");
        dto.setCreated(LocalDateTime.now());

        given(itemRequestService.findAllOtherUsers(1L)).willReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));

        verify(itemRequestService).findAllOtherUsers(1L);
    }

    @Test
    void findById_whenNotFound_shouldReturnNotFound() throws Exception {
        given(itemRequestService.findById(1L, 2L))
                .willThrow(new NotFoundException("Такого запроса нет!"));

        mockMvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$['Not found']").value("Такого запроса нет!"));
    }
}