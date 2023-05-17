package ru.yandex.yandexlavka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.domain.DeliveryTime;
import ru.yandex.yandexlavka.domain.Shift;

import java.util.Optional;

@Repository
public interface DeliveryTimesRepository extends JpaRepository<DeliveryTime, Integer> {
    Optional<DeliveryTime> findDeliveryTimeByDeliveryHours(String deliveryHours);
}
