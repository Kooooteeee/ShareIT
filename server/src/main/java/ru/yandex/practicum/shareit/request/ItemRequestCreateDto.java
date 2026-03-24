package ru.yandex.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestCreateDto {

    private String description;
}