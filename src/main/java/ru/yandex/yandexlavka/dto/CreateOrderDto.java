package ru.yandex.yandexlavka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderDto(@JsonProperty("weight") @Positive Float weight,
                             @JsonProperty("regions") Integer regions,
                             @JsonProperty("delivery_hours") @NotEmpty
                             List<@Pattern(regexp = "([0-1]\\d|2[0-3]):[0-5]\\d-([0-1]\\d|2[0-3]):[0-5]\\d") String>
                                     deliveryHours,
                             @JsonProperty("cost") @Positive Integer cost) {
}
