package ru.yandex.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Test
    void serialize_shouldWriteDatesInExpectedFormat() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 3, 25, 12, 30, 0));
        dto.setEnd(LocalDateTime.of(2026, 3, 26, 14, 0, 0));

        JsonContent<BookingCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-03-25T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-03-26T14:00:00");
    }

    @Test
    void deserialize_shouldReadDatesInExpectedFormat() throws Exception {
        String content = """
                {
                  "itemId": 1,
                  "start": "2026-03-25T12:30:00",
                  "end": "2026-03-26T14:00:00"
                }
                """;

        BookingCreateDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 3, 25, 12, 30, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 3, 26, 14, 0, 0));
    }
}