package ru.yandex.practicum.shareit.request;

import lombok.Data;

@Data
public class ItemForRequestDto {

    private Long id;
    private String name;
    private Long ownerId;
}