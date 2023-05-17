package ru.yandex.yandexlavka.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public record OrderDto(@JsonProperty("order_id") Long id,
                       @JsonProperty("weight") Float weight,
                       @JsonProperty("regions") Integer regions,
                       @JsonProperty("delivery_hours") List<String> deliveryHours,
                       @JsonProperty("cost") Integer cost,
                       @JsonProperty("completed_time") @JsonInclude(JsonInclude.Include.NON_NULL) String completedTime) {
}
