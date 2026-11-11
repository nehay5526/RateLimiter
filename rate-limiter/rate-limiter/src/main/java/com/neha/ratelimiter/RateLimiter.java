package com.neha.ratelimiter;

public interface RateLimiter {

    /** @return true if the request is allowed, false if it should be rejected */
    boolean tryAcquire();
}
