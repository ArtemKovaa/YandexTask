package ru.yandex.yandexlavka.requests;

import jakarta.validation.Valid;
import ru.yandex.yandexlavka.dto.CreateCourierDto;

import java.util.List;

public record CreateCouriersRequest(List<@Valid CreateCourierDto> couriers) {
}
