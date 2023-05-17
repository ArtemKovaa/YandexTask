package ru.yandex.yandexlavka.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.yandexlavka.dto.CompleteOrderDto;

import java.util.List;

public record CompleteOrdersRequest(@JsonProperty("complete_info") List<CompleteOrderDto> completeInfo) {
}
