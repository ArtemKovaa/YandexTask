package ru.yandex.yandexlavka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.yandexlavka.domain.Region;

import java.util.Optional;

public interface RegionsRepository extends JpaRepository<Region, Integer> {
    Optional<Region> findRegionById(Integer id);
}
