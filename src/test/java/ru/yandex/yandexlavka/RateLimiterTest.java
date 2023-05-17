package ru.yandex.yandexlavka;

import org.junit.jupiter.api.Test;
import ru.yandex.yandexlavka.utils.RateLimiter;

import static org.junit.jupiter.api.Assertions.*;


public class RateLimiterTest {
    private final RateLimiter rateLimiter = new RateLimiter();

    @Test
    void rateLimiter_shouldControlConsumer() {
        // given
        var method = "POST /couriers";

        // when and then
        for (int i = 0; i < 10; i++) {
            assertTrue(rateLimiter.tryConsume(method), "Bucket overflowed");
        }
        assertFalse(rateLimiter.tryConsume(method), "Bucket limit not working");
    }
}
