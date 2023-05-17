package ru.yandex.yandexlavka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.domain.Courier;

import java.util.List;

@Repository
public interface CouriersRepository extends JpaRepository<Courier, Long> {
    @Query(value = "SELECT * FROM courier c ORDER BY c.id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Courier> findAllPaginated(Integer limit, Integer offset);
}
