package com.neha.ratelimiter;

import java.time.Duration;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = new FixedWindowRateLimiter(3, Duration.ofSeconds(1));

        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " + (limiter.tryAcquire() ? "ALLOWED" : "REJECTED"));
        }

        System.out.println("Sleeping 1.1s for the next window...");
        Thread.sleep(1100);

        for (int i = 6; i <= 8; i++) {
            System.out.println("Request " + i + ": " + (limiter.tryAcquire() ? "ALLOWED" : "REJECTED"));
        }


         SimpleRateLimiter simpleLimiter = new SimpleRateLimiter(3, 1000);

        for (int i = 1; i <= 5; i++) {
            System.out.println("Request simpleLimiter " + i + ": " + simpleLimiter.tryAcquire());
        }
        // true, true, true, false, false

        Thread.sleep(1100);

        System.out.println("After 1.1s:simpleLimiter  " + simpleLimiter.tryAcquire()); // true
    }
}
