package ru.yandex.yandexlavka.utils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public boolean tryConsume(String handler) {
        Bucket bucket = cache.computeIfAbsent(handler, this::newBucket);
        return bucket.tryConsume(1);
    }

    private Bucket newBucket(String handler) {
        Refill refill = Refill.intervally(10, Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(10, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
