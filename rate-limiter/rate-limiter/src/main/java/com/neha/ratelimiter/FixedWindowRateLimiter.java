package com.neha.ratelimiter;

import java.time.Duration;
import java.util.function.LongSupplier;

/**
 * Stage 1: fixed window counter.
 * Allows at most maxRequests per window. Thread-safe via synchronized.
 */
public class FixedWindowRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowNanos;
    private final LongSupplier nanoClock;

    private long windowStart;
    private int count;

    public FixedWindowRateLimiter(int maxRequests, Duration window) {
        this(maxRequests, window, System::nanoTime);
    }

    // Package-private: lets tests control time without Thread.sleep
    FixedWindowRateLimiter(int maxRequests, Duration window, LongSupplier nanoClock) {
        if (maxRequests <= 0) throw new IllegalArgumentException("maxRequests must be > 0");
        if (window.isZero() || window.isNegative()) throw new IllegalArgumentException("window must be > 0");
        this.maxRequests = maxRequests;
        this.windowNanos = window.toNanos();
        this.nanoClock = nanoClock;
        this.windowStart = nanoClock.getAsLong();
    }

    @Override
    public synchronized boolean tryAcquire() {
        long now = nanoClock.getAsLong();
        if (now - windowStart >= windowNanos) {
            windowStart = now;
            count = 0;
        }
        if (count < maxRequests) {
            count++;
            return true;
        }
        return false;
    }
}
