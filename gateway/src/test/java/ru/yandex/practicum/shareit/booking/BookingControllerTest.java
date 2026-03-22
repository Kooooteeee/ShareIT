package ru.yandex.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.exception.ErrorHandler;

import java.time.LocalDateTime;
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

@WebMvcTest(BookingController.class)
@Import(ErrorHandler.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void create_shouldReturnOk() throws Exception {
        BookingCreateDto dto = validBookingCreateDto();

        given(bookingClient.create(eq(1L), any(BookingCreateDto.class)))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "status", "WAITING"
                )));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingClient).create(eq(1L), any(BookingCreateDto.class));
    }

    @Test
    void create_withPastStart_shouldReturnBadRequest() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().minusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    void approve_shouldReturnOk() throws Exception {
        given(bookingClient.approve(1L, 2L, true))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 2L,
                        "status", "APPROVED"
                )));

        mockMvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingClient).approve(1L, 2L, true);
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        given(bookingClient.getById(1L, 2L))
                .willReturn(ResponseEntity.ok(Map.of(
                        "id", 2L,
                        "status", "WAITING"
                )));

        mockMvc.perform(get("/bookings/2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));

        verify(bookingClient).getById(1L, 2L);
    }

    @Test
    void getByBooker_shouldReturnOk() throws Exception {
        given(bookingClient.getByBooker(1L, BookingState.ALL))
                .willReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 2L, "status", "WAITING")
                )));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));

        verify(bookingClient).getByBooker(1L, BookingState.ALL);
    }

    @Test
    void getByOwner_withUnknownState_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "WRONG"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Unknown state: WRONG")));
    }

    private BookingCreateDto validBookingCreateDto() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }
}