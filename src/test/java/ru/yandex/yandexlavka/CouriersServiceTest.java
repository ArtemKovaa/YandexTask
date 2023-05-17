package ru.yandex.yandexlavka;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.yandexlavka.domain.Courier;
import ru.yandex.yandexlavka.domain.Region;
import ru.yandex.yandexlavka.domain.Shift;
import ru.yandex.yandexlavka.dto.CreateCourierDto;
import ru.yandex.yandexlavka.exceptions.NotFoundException;
import ru.yandex.yandexlavka.repositories.CouriersRepository;
import ru.yandex.yandexlavka.repositories.OrdersRepository;
import ru.yandex.yandexlavka.repositories.RegionsRepository;
import ru.yandex.yandexlavka.repositories.ShiftsRepository;
import ru.yandex.yandexlavka.services.CouriersService;

import java.util.List;
import java.util.Optional;

import static ru.yandex.yandexlavka.domain.Courier.Type;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouriersServiceTest {
    @InjectMocks
    private CouriersService couriersService;

    @Mock
    private CouriersRepository couriersRepository;
    @Mock
    private RegionsRepository regionsRepository;
    @Mock
    private ShiftsRepository shiftsRepository;
    @Mock
    private OrdersRepository ordersRepository;

    private static final Courier courier = new Courier();
    private static final Region region = new Region();
    private static final Shift shift = new Shift();
    private static long id = 0L;

    @BeforeAll
    static void init() {
        region.setId(1);
        shift.setWorkingHours("08:00-12:00");

        courier.setType(Type.AUTO);
        courier.setId(id);
        courier.addWorkingHours(shift);
        courier.addRegion(region);
    }

    @Test
    void createCouriers_shouldReturnListOfCourierDto() {
        // given
        var couriers = List.of(new CreateCourierDto(Type.AUTO, List.of(1), List.of("08:00-12:00")));

        when(couriersRepository.save(any())).thenReturn(courier);

        // when
        var response = couriersService.createCouriers(couriers).couriers().get(0);

        // then
        assertAll( "Assert create couriers response",
                () -> assertEquals(courier.getId(), response.id(), "Id is wrong"),
                () -> assertEquals(courier.getType(), response.type(), "Type is wrong"),
                () -> assertEquals(courier.getRegionsId(), response.regions(), "Regions is wrong"),
                () -> assertEquals(courier.getWorkingHours(), response.workingHours(), "Working hours is wrong")
        );
    }

    @Test
    void getSingleCourier_shouldReturnCourierDto() {
        // given
        when(couriersRepository.findById(any())).thenReturn(Optional.of(courier));

        // when
        var response = couriersService.getSingleCourier(id);

        // then
        assertAll("Assert getting of single courier response",
                () -> assertEquals(courier.getId(), response.id(), "Id is wrong"),
                () -> assertEquals(courier.getType(), response.type(), "Type is wrong"),
                () -> assertEquals(courier.getRegionsId(), response.regions(), "Regions is wrong"),
                () -> assertEquals(courier.getWorkingHours(), response.workingHours(), "Working hours is wrong")
        );
    }

    @Test
    void getSingleCourier_shouldThrowException() {
        // given
        when(couriersRepository.findById(any())).thenReturn(Optional.empty());

        // when
        var throwable = assertThrows(NotFoundException.class, () -> couriersService.getSingleCourier(id));

        // then
        assertNotNull(throwable, "No NotFoundException thrown");
    }

    @Test
    void getCourier_shouldReturnGetCouriersResponse() {
        // given
        int limit = 1, offset = 0;

        when(couriersRepository.findAllPaginated(any(), any())).thenReturn(List.of(courier));

        // when
        var response = couriersService.getCouriers(limit, offset);

        // then
        assertAll("Assert getting of couriers list",
                () -> assertEquals(limit, response.limit(), "Limit is wrong"),
                () -> assertEquals(offset, response.offset(), "Offset is wrong"),
                () -> assertEquals(limit, response.couriers().size(), "Count of couriers is wrong"),
                () -> assertEquals(courier.getId(), response.couriers().get(0).id(), "Id is wrong"),
                () -> assertEquals(courier.getType(), response.couriers().get(0).type(), "Type is wrong"),
                () -> assertEquals(courier.getWorkingHours(), response.couriers().get(0).workingHours(), "Working hours are wrong"),
                () -> assertEquals(courier.getRegionsId(), response.couriers().get(0).regions(), "Regions are wrong")
        );
    }
}
