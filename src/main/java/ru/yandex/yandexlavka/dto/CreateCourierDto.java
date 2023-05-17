package ru.yandex.yandexlavka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

import static ru.yandex.yandexlavka.domain.Courier.Type;

public record CreateCourierDto(@JsonProperty("courier_type") Type type,
                               @JsonProperty("regions") @NotEmpty List<Integer> regions,
                               @JsonProperty("working_hours") @NotEmpty
                               List<@Pattern(regexp = "([0-1]\\d|2[0-3]):[0-5]\\d-([0-1]\\d|2[0-3]):[0-5]\\d") String>
                                       workingHours) {
}
