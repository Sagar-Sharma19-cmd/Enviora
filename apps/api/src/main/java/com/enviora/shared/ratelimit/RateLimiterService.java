package com.enviora.shared.ratelimit;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private static final int MAX_REQUESTS = 3;
    private static final long WINDOW_SECONDS = 900; // 15 minutes

    private final Map<String, List<Instant>> requestHistory = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key) {
        Instant now = Instant.now();
        Instant cutoff = now.minusSeconds(WINDOW_SECONDS);

        requestHistory.compute(key, (k, timestamps) -> {
            if (timestamps == null) {
                timestamps = new ArrayList<>();
            }
            timestamps.removeIf(t -> t.isBefore(cutoff));
            return timestamps;
        });

        List<Instant> timestamps = requestHistory.get(key);
        if (timestamps.size() >= MAX_REQUESTS) {
            return false;
        }

        timestamps.add(now);
        return true;
    }
}
