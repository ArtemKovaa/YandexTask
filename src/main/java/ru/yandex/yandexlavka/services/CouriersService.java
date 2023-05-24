package ru.yandex.yandexlavka.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.yandexlavka.domain.Courier;
import ru.yandex.yandexlavka.domain.Region;
import ru.yandex.yandexlavka.domain.Shift;
import ru.yandex.yandexlavka.dto.CourierDto;
import ru.yandex.yandexlavka.dto.CreateCourierDto;
import ru.yandex.yandexlavka.exceptions.NotFoundException;
import ru.yandex.yandexlavka.repositories.CouriersRepository;
import ru.yandex.yandexlavka.repositories.OrdersRepository;
import ru.yandex.yandexlavka.repositories.RegionsRepository;
import ru.yandex.yandexlavka.repositories.ShiftsRepository;
import ru.yandex.yandexlavka.responses.CreateCouriersResponse;
import ru.yandex.yandexlavka.responses.GetCourierMetaInfoResponse;
import ru.yandex.yandexlavka.responses.GetCouriersResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CouriersService {
    private final CouriersRepository couriersRepository;
    private final RegionsRepository regionsRepository;
    private final ShiftsRepository shiftsRepository;
    private final OrdersRepository ordersRepository;

    @Transactional
    public CreateCouriersResponse createCouriers(List<CreateCourierDto> couriers) {
        List<CourierDto> response = new ArrayList<>();
        couriers.forEach(dto -> {
            var courier = new Courier();
            courier.setType(dto.type());
            dto.workingHours().forEach(wh -> addWorkingHours(courier, wh));
            dto.regions().forEach(id -> addRegion(courier, id));
            response.add(mapCourierToDto(couriersRepository.save(courier)));
        });
        return new CreateCouriersResponse(response);
    }

    private void addWorkingHours(Courier courier, String workingHours) {
        Optional<Shift> optionalShift = shiftsRepository.findShiftByWorkingHours(workingHours);
        if (optionalShift.isPresent()) {
            courier.addWorkingHours(optionalShift.get());
        } else {
            var shift = new Shift();
            shift.setWorkingHours(workingHours);
            courier.addWorkingHours(shift);
        }
    }

    private void addRegion(Courier courier, Integer regionId) {
        Optional<Region> optionalRegion = regionsRepository.findRegionById(regionId);
        if (optionalRegion.isPresent()) {
            courier.addRegion(optionalRegion.get());
        } else {
            var region = new Region();
            region.setId(regionId);
            courier.addRegion(region);
        }
    }

    private CourierDto mapCourierToDto(Courier courier) {
        return new CourierDto(courier.getId(), courier.getType(), courier.getRegionsId(), courier.getWorkingHours());
    }

    @Transactional(readOnly = true)
    public CourierDto getSingleCourier(Long id) {
        return mapCourierToDto(findCourier(id));
    }

    @Transactional(readOnly = true)
    public GetCouriersResponse getCouriers(Integer limit, Integer offset) {
        List<CourierDto> couriers = new ArrayList<>();
        couriersRepository.findAllPaginated(limit, offset)
                .forEach(c -> couriers.add(mapCourierToDto(c)));
        return new GetCouriersResponse(couriers, limit, offset);
    }

    @Transactional(readOnly = true)
    public GetCourierMetaInfoResponse getMetaInfo(Long id, String start, String end) throws ParseException {
        var formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date startDate = formatter.parse(start);
        Date endDate = formatter.parse(end);

        var courier = findCourier(id);
        Integer rating = getCourierRating(id, startDate, endDate, courier);
        Integer earnings = getCourierEarnings(id, startDate, endDate, courier);

        return new GetCourierMetaInfoResponse(courier.getId(), courier.getType(), courier.getRegionsId(),
                courier.getWorkingHours(), rating, earnings);
    }

    private Courier findCourier(Long id) {
        Optional<Courier> optionalCourier = couriersRepository.findById(id);
        if (optionalCourier.isPresent()) {
            return optionalCourier.get();
        } else {
            throw new NotFoundException();
        }
    }

    private Integer getCourierRating(Long id, Date startDate, Date endDate, Courier courier) {
        Integer ordersCount = ordersRepository.countOrdersInPeriodByCourierId(id, startDate, endDate);
        if (ordersCount > 0) {
            long differenceInMillis = endDate.getTime() - startDate.getTime();
            int preResult = ordersCount / (int) TimeUnit.MILLISECONDS.toHours(differenceInMillis);
            return preResult * courier.getType().getRatingQuotient();
        } else {
            return null;
        }
    }

    private Integer getCourierEarnings(Long id, Date startDate, Date endDate, Courier courier) {
        Optional<Integer> optionalEarnings = ordersRepository.getEarningsInPeriodByCourierId(id, startDate, endDate);
        return optionalEarnings.map(earnings -> earnings * courier.getType().getEarningsQuotient())
                .orElse(null);
    }
}
