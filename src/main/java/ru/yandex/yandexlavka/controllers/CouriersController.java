package ru.yandex.yandexlavka.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.dto.CourierDto;
import ru.yandex.yandexlavka.requests.CreateCouriersRequest;
import ru.yandex.yandexlavka.responses.CreateCouriersResponse;
import ru.yandex.yandexlavka.responses.GetCourierMetaInfoResponse;
import ru.yandex.yandexlavka.responses.GetCouriersResponse;
import ru.yandex.yandexlavka.services.CouriersService;
import ru.yandex.yandexlavka.utils.RateLimiter;

import java.text.ParseException;

@RestController
@RequestMapping("/couriers")
@Validated
@RequiredArgsConstructor
public class CouriersController {
    private final CouriersService couriersService;
    private final RateLimiter rateLimiter;

    @PostMapping
    public ResponseEntity<CreateCouriersResponse> createCouriers(@Valid @RequestBody CreateCouriersRequest request) {
        if (rateLimiter.tryConsume("POST /couriers")) {
            return ResponseEntity.ok(couriersService.createCouriers(request.couriers()));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping("/{courier_id}")
    public ResponseEntity<CourierDto> getSingleCourier(@PathVariable("courier_id") Long id) {
        if (rateLimiter.tryConsume("GET /couriers/{courier_id}")) {
            return ResponseEntity.ok(couriersService.getSingleCourier(id));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }


    @GetMapping
    public ResponseEntity<GetCouriersResponse> getCouriers(@Positive @RequestParam(defaultValue = "1") Integer limit,
                                                           @Min(0) @RequestParam(defaultValue = "0") Integer offset) {
        if (rateLimiter.tryConsume("GET /couriers")) {
            return ResponseEntity.ok(couriersService.getCouriers(limit, offset));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping("/meta-info/{courier_id}")
    public ResponseEntity<GetCourierMetaInfoResponse> getMetaInfo(@PathVariable("courier_id") Long id,
                                                                 @RequestParam("start_date") String startDate,
                                                                 @RequestParam("end_date") String endDate) throws ParseException {
        if (rateLimiter.tryConsume("GET /couriers/meta-info/{courier_id}")) {
            return ResponseEntity.ok(couriersService.getMetaInfo(id, startDate, endDate));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
}