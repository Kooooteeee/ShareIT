package ru.yandex.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareit.exception.BadRequestException;
import ru.yandex.practicum.shareit.exception.ErrorHandler;
import ru.yandex.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

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
    private BookingService bookingService;

    @Test
    void create_shouldReturnOk() throws Exception {
        BookingCreateDto request = validBookingCreateDto();

        BookingDto response = new BookingDto();
        response.setId(1L);
        response.setStart(request.getStart());
        response.setEnd(request.getEnd());
        response.setStatus("WAITING");

        given(bookingService.create(eq(1L), any(BookingCreateDto.class))).willReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService).create(eq(1L), any(BookingCreateDto.class));
    }

    @Test
    void create_whenBadRequest_shouldReturnBadRequest() throws Exception {
        given(bookingService.create(eq(1L), any(BookingCreateDto.class)))
                .willThrow(new BadRequestException("Некорректное бронирование"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookingCreateDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Некорректное бронирование"));
    }

    @Test
    void approve_shouldReturnOk() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(2L);
        response.setStatus("APPROVED");

        given(bookingService.approve(1L, 2L, true)).willReturn(response);

        mockMvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).approve(1L, 2L, true);
    }

    @Test
    void getById_whenNotFound_shouldReturnNotFound() throws Exception {
        given(bookingService.getById(1L, 2L))
                .willThrow(new NotFoundException("Такого бронирования нет!"));

        mockMvc.perform(get("/bookings/2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$['Not found']").value("Такого бронирования нет!"));
    }

    @Test
    void getByBooker_shouldReturnOk() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(2L);
        dto.setStatus("WAITING");

        given(bookingService.getByBooker(1L, "ALL")).willReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));

        verify(bookingService).getByBooker(1L, "ALL");
    }

    @Test
    void getByOwner_shouldReturnOk() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(3L);
        dto.setStatus("WAITING");

        given(bookingService.getByOwner(1L, "CURRENT")).willReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));

        verify(bookingService).getByOwner(1L, "CURRENT");
    }

    private BookingCreateDto validBookingCreateDto() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }
}