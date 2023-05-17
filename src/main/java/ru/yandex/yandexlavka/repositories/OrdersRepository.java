package ru.yandex.yandexlavka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.yandexlavka.domain.Order;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT * FROM orders o ORDER BY o.id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Order> findAllPaginated(Integer limit, Integer offset);
    @Query(value = "SELECT SUM(cost) FROM orders WHERE courier_id = :id AND " +
            "completed_time >= :startDate AND completed_time < :endDate", nativeQuery = true)
    Optional<Integer> getEarningsInPeriodByCourierId(Long id, Date startDate, Date endDate);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE courier_id = :id AND " +
            "completed_time >= :startDate AND completed_time < :endDate", nativeQuery = true)
    Integer countOrdersInPeriodByCourierId(Long id, Date startDate, Date endDate);
}
