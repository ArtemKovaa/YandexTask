package ru.yandex.yandexlavka.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import static ru.yandex.yandexlavka.domain.Courier.Type;

import java.util.List;

public record GetCourierMetaInfoResponse(@JsonProperty("courier_id") Long id,
                                         @JsonProperty("courier_type") Type type,
                                         @JsonProperty("regions") List<Integer> regions,
                                         @JsonProperty("working_hours") List<String> workingHours,
                                         @JsonProperty("rating") @JsonInclude(JsonInclude.Include.NON_NULL) Integer rating,
                                         @JsonProperty("earnings") @JsonInclude(JsonInclude.Include.NON_NULL) Integer earnings) {
}
