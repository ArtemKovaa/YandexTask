package ru.yandex.yandexlavka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import static ru.yandex.yandexlavka.domain.Courier.Type;

import java.util.List;

public record CourierDto(@JsonProperty("courier_id") Long id,
                         @JsonProperty("courier_type") Type type,
                         @JsonProperty("regions") List<Integer> regions,
                         @JsonProperty("working_hours") List<String> workingHours) {
}
