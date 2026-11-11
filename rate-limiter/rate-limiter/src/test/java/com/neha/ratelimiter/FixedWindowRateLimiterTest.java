package com.neha.ratelimiter;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class FixedWindowRateLimiterTest {

    @Test
    void allowsUpToLimitThenRejects() {
        RateLimiter limiter = new FixedWindowRateLimiter(3, Duration.ofSeconds(1));
        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertFalse(limiter.tryAcquire());
    }

    @Test
    void resetsAfterWindowPasses() {
        AtomicLong fakeNanos = new AtomicLong(0);
        RateLimiter limiter = new FixedWindowRateLimiter(2, Duration.ofSeconds(1), fakeNanos::get);

        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertFalse(limiter.tryAcquire());

        fakeNanos.addAndGet(Duration.ofSeconds(1).toNanos()); // advance one full window

        assertTrue(limiter.tryAcquire());
    }

    @Test
    void neverExceedsLimitUnderConcurrency() throws Exception {
        int limit = 100;
        RateLimiter limiter = new FixedWindowRateLimiter(limit, Duration.ofMinutes(1));
        AtomicInteger allowed = new AtomicInteger();

        int threads = 16;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < 100; i++) {
                        if (limiter.tryAcquire()) allowed.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        assertTrue(done.await(10, TimeUnit.SECONDS));
        pool.shutdown();

        assertEquals(limit, allowed.get());
    }

    @Test
    void rejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new FixedWindowRateLimiter(0, Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class, () -> new FixedWindowRateLimiter(1, Duration.ZERO));
    }
}
