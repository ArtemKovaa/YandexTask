package ru.yandex.yandexlavka.responses;

import ru.yandex.yandexlavka.dto.CourierDto;

import java.util.List;

public record GetCouriersResponse(List<CourierDto> couriers, Integer limit, Integer offset) {
}
