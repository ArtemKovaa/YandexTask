package ru.yandex.yandexlavka.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.dto.OrderDto;
import ru.yandex.yandexlavka.requests.CompleteOrdersRequest;
import ru.yandex.yandexlavka.requests.CreateOrdersRequest;
import ru.yandex.yandexlavka.services.OrdersService;
import ru.yandex.yandexlavka.utils.RateLimiter;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService ordersService;
    private final RateLimiter rateLimiter;

    @PostMapping
    public ResponseEntity<List<OrderDto>> createOrders(@Valid @RequestBody CreateOrdersRequest request) {
        if (rateLimiter.tryConsume("POST /orders")) {
            return ResponseEntity.ok(ordersService.createOrders(request.orders()));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping("/{order_id}")
    public ResponseEntity<OrderDto> getSingleOrder(@PathVariable("order_id") Long id) {
        if (rateLimiter.tryConsume("GET /orders/{order_id}")) {
            return ResponseEntity.ok(ordersService.getSingleOrder(id));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(@Positive @RequestParam(defaultValue = "1") Integer limit,
                                                    @Min(0) @RequestParam(defaultValue = "0") Integer offset) {
        if (rateLimiter.tryConsume("GET /orders")) {
            return ResponseEntity.ok(ordersService.getOrders(limit, offset));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<List<OrderDto>> completeOrders(@RequestBody CompleteOrdersRequest request) {
        if (rateLimiter.tryConsume("POST /orders/complete")) {
            return ResponseEntity.ok(ordersService.completeOrders(request.completeInfo()));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
}
