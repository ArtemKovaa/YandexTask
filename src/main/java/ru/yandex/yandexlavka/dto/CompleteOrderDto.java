package ru.yandex.yandexlavka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompleteOrderDto(@JsonProperty("courier_id") Long courierId,
                               @JsonProperty("order_id") Long orderId,
                               @JsonProperty("complete_time") String completeTime) {
}
