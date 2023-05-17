package ru.yandex.yandexlavka.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.domain.Courier;
import ru.yandex.yandexlavka.domain.DeliveryTime;
import ru.yandex.yandexlavka.domain.Order;
import ru.yandex.yandexlavka.domain.Region;
import ru.yandex.yandexlavka.dto.CompleteOrderDto;
import ru.yandex.yandexlavka.dto.CreateOrderDto;
import ru.yandex.yandexlavka.dto.OrderDto;
import ru.yandex.yandexlavka.exceptions.NotFoundException;
import ru.yandex.yandexlavka.exceptions.OrderException;
import ru.yandex.yandexlavka.repositories.CouriersRepository;
import ru.yandex.yandexlavka.repositories.DeliveryTimesRepository;
import ru.yandex.yandexlavka.repositories.OrdersRepository;
import ru.yandex.yandexlavka.repositories.RegionsRepository;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final RegionsRepository regionsRepository;
    private final CouriersRepository couriersRepository;
    private final DeliveryTimesRepository deliveryTimesRepository;

    @Transactional
    public List<OrderDto> createOrders(List<CreateOrderDto> orders) {
        List<OrderDto> response = new ArrayList<>();
        orders.forEach(dto -> {
            var order = new Order();
            order.setWeight(dto.weight());
            order.setCost(dto.cost());
            order.setCompletedTime(null);
            addRegion(order, dto.regions());
            dto.deliveryHours().forEach(dh -> addDeliveryHours(order, dh));
            response.add(mapOrderToDto(ordersRepository.save(order)));
        });
        return response;
    }

    private OrderDto mapOrderToDto(Order order) {
        String completedTime = null;
        if (Objects.nonNull(order.getCompletedTime())) {
            var formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            completedTime = formatter.format(order.getCompletedTime());
        }

        return new OrderDto(order.getId(), order.getWeight(), order.getRegionId(),
                order.getDeliveryHours(), order.getCost(), completedTime);
    }

    private void addRegion(Order order, Integer regionId) {
        Optional<Region> optionalRegion = regionsRepository.findRegionById(regionId);
        if (optionalRegion.isPresent()) {
            order.addRegion(optionalRegion.get());
        } else {
            var region = new Region();
            region.setId(regionId);
            order.addRegion(region);
        }
    }

    private void addDeliveryHours(Order order, String deliveryHours) {
        Optional<DeliveryTime> optionalDeliveryTime =
                deliveryTimesRepository.findDeliveryTimeByDeliveryHours(deliveryHours);
        if (optionalDeliveryTime.isPresent()) {
            order.addDeliveryTime(optionalDeliveryTime.get());
        } else {
            var deliveryTime = new DeliveryTime();
            deliveryTime.setDeliveryHours(deliveryHours);
            order.addDeliveryTime(deliveryTime);
        }
    }

    @Transactional(readOnly = true)
    public OrderDto getSingleOrder(Long id) {
        var optionalOrder = ordersRepository.findById(id);
        if (optionalOrder.isPresent()) {
            return mapOrderToDto(optionalOrder.get());
        } else {
            throw new NotFoundException();
        }
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrders(Integer limit, Integer offset) {
        List<OrderDto> orders = new ArrayList<>();
        ordersRepository.findAllPaginated(limit, offset)
                .forEach(o -> orders.add(mapOrderToDto(o)));
        return orders;
    }

    @Transactional
    public List<OrderDto> completeOrders(List<CompleteOrderDto> completeInfo) {
        List<OrderDto> orders = new ArrayList<>();
        completeInfo.forEach(ci -> {
            var optionalOrder = ordersRepository.findById(ci.orderId());
            if (optionalOrder.isEmpty()) {
                throw new NotFoundException();
            }

            var order = optionalOrder.get();
            if (order.getCourier() == null || !Objects.equals(order.getCourier().getId(), ci.courierId())) {
                throw new OrderException();
            }

            var formatter = DateTimeFormatter.ISO_INSTANT;
            formatter.withZone(ZoneId.of("UTC"));
            var date = Date.from(Instant.from(formatter.parse(ci.completeTime())));
            order.setCompletedTime(date);

            orders.add(mapOrderToDto(order));
        });
        return orders;
    }
}
