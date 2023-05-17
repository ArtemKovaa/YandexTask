package ru.yandex.yandexlavka.requests;

import jakarta.validation.Valid;
import ru.yandex.yandexlavka.dto.CreateOrderDto;

import java.util.List;

public record CreateOrdersRequest(List<@Valid CreateOrderDto> orders) {
}
