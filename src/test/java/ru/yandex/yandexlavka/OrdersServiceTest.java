package ru.yandex.yandexlavka;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.yandexlavka.domain.*;
import ru.yandex.yandexlavka.dto.CompleteOrderDto;
import ru.yandex.yandexlavka.dto.CreateOrderDto;
import ru.yandex.yandexlavka.exceptions.NotFoundException;
import ru.yandex.yandexlavka.exceptions.OrderException;
import ru.yandex.yandexlavka.repositories.DeliveryTimesRepository;
import ru.yandex.yandexlavka.repositories.OrdersRepository;
import ru.yandex.yandexlavka.repositories.RegionsRepository;
import ru.yandex.yandexlavka.services.OrdersService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.yandex.yandexlavka.domain.Courier.Type;

@ExtendWith(MockitoExtension.class)
public class OrdersServiceTest {

    @InjectMocks
    private OrdersService ordersService;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private RegionsRepository regionsRepository;
    @Mock
    private DeliveryTimesRepository deliveryTimesRepository;

    private static final Order order = new Order();
    private static final Courier courier = new Courier();
    private static final Shift shift = new Shift();
    private static final Region region = new Region();
    private static final DeliveryTime deliveryTime = new DeliveryTime();
    private static final long id = 0L;

    @BeforeAll
    static void init() {
        region.setId(0);
        deliveryTime.setDeliveryHours("15:00-17:00");

        order.setId(id);
        order.setWeight(0.5f);
        order.setCost(300);
        order.addRegion(region);
        order.addDeliveryTime(deliveryTime);

        shift.setWorkingHours("08:00-18:00");

        courier.setType(Type.AUTO);
        courier.setId(id);
        courier.addWorkingHours(shift);
        courier.addRegion(region);
    }

    @Test
    void createOrders_shouldReturnListOfOrderDto() {
        // given
        var orders = List.of(new CreateOrderDto(0.5f, 0, List.of("15:00-17:00"), 300));

        when(ordersRepository.save(any())).thenReturn(order);

        // when
        var response = ordersService.createOrders(orders).get(0);

        // then
        assertAll("Assert create orders response",
                () -> assertEquals(order.getId(), response.id(), "Id is wrong"),
                () -> assertEquals(order.getWeight(), response.weight(), "Weight is wrong"),
                () -> assertEquals(order.getCost(), response.cost(), "Cost is wrong"),
                () -> assertNull(response.completedTime(), "Completed time is wrong"),
                () -> assertEquals(order.getRegionId(), response.regions(), "Region is wrong")
        );
    }

    @Test
    void getSingleOrder_shouldReturnOrderDtoWithNullCompletedTime() {
        // given
        when(ordersRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        var response = ordersService.getSingleOrder(id);

        // then
        assertAll("Assert getting of single order response",
                () -> assertEquals(order.getId(), response.id(), "Id is wrong"),
                () -> assertEquals(order.getCost(), response.cost(), "Cost is wrong"),
                () -> assertEquals(order.getRegionId(), response.regions(), "Region is wrong"),
                () -> assertEquals(order.getWeight(), response.weight(), "Weight is wrong"),
                () -> assertEquals(order.getDeliveryHours(), response.deliveryHours(), "Delivery hours is wrong"),
                () -> assertNull(response.completedTime(), "Completed time is wrong")
        );
    }

    @Test
    void getSingleOrder_shouldThrowException() {
        // given
        when(ordersRepository.findById(any())).thenReturn(Optional.empty());

        // when
        var throwable = assertThrows(NotFoundException.class, () -> ordersService.getSingleOrder(id));

        // then
        assertNotNull(throwable, "No NotFoundException thrown");
    }

    @Test
    void getOrders_shouldReturnListOfOrderDto() {
        // given
        int limit = 1, offset = 0;

        when(ordersRepository.findAllPaginated(any(), any())).thenReturn(List.of(order));

        // when
        var response = ordersService.getOrders(limit, offset).get(0);

        // then
        assertAll("Assert getting of orders list",
                () -> assertEquals(order.getId(), response.id(), "Id is wrong"),
                () -> assertEquals(order.getCost(), response.cost(), "Cost is wrong"),
                () -> assertEquals(order.getRegionId(), response.regions(), "Region is wrong"),
                () -> assertEquals(order.getWeight(), response.weight(), "Weight is wrong"),
                () -> assertEquals(order.getDeliveryHours(), response.deliveryHours(), "Delivery hours are wrong"),
                () -> assertNull(response.completedTime(), "Completed time is wrong")
        );
    }

    @Test
    void completeOrders_shouldThrowNotFoundException() {
        // given
        List<CompleteOrderDto> completeInfo = List.of(
                new CompleteOrderDto(0L, 0L, "2023-05-04T10:13:13.067Z"));

        when(ordersRepository.findById(any())).thenReturn(Optional.empty());

        // when
        var throwable = assertThrows(NotFoundException.class, () -> ordersService.completeOrders(completeInfo));

        // then
        assertNotNull(throwable, "No NotFoundException thrown");
    }

    @Test
    void completeOrders_shouldThrowOrderException() {
        // given
        List<CompleteOrderDto> completeInfo = List.of(
                new CompleteOrderDto(0L, 0L, "2023-05-04T10:13:13.067Z"));

        when(ordersRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        var throwable = assertThrows(OrderException.class, () -> ordersService.completeOrders(completeInfo));

        // then
        assertNotNull(throwable, "No OrderException thrown");
    }

    @Test
    void completeOrders_shouldReturnListOfOrderDto() {
        // given
        List<CompleteOrderDto> completeInfo = List.of(
                new CompleteOrderDto(0L, 0L, "2023-05-04T10:13:13.067Z"));
        order.setCourier(courier); // assign courier

        when(ordersRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        var response = ordersService.completeOrders(completeInfo).get(0);

        // then
        assertAll("Assert getting of orders list",
                () -> assertEquals(order.getId(), response.id(), "Id is wrong"),
                () -> assertEquals(order.getCost(), response.cost(), "Cost is wrong"),
                () -> assertEquals(order.getRegionId(), response.regions(), "Region is wrong"),
                () -> assertEquals(order.getWeight(), response.weight(), "Weight is wrong"),
                () -> assertEquals(order.getDeliveryHours(), response.deliveryHours(), "Delivery hours are wrong"),
                () -> assertEquals("2023-05-04T13:13:13.067Z", response.completedTime(), "Completed time is wrong"),
                () -> assertEquals(order.getCourier(), courier, "Courier is wrong"));
    }
}
