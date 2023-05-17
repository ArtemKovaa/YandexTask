package ru.yandex.yandexlavka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.yandexlavka.domain.Shift;

import java.util.Optional;

public interface ShiftsRepository extends JpaRepository<Shift, Integer> {
    Optional<Shift> findShiftByWorkingHours(String workingHours);
}
