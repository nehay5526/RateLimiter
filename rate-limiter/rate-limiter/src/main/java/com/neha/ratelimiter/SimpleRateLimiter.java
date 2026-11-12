package com.neha.ratelimiter;

public class SimpleRateLimiter {

    private final int maxRequestsPerSecond;
    private final long windowSizeInMilliseconds;

    private long windowStartTime = System.currentTimeMillis();
    private int requestCount =0;

    public SimpleRateLimiter(int maxRequestPerSeconds,long windowSizeInMilliseconds) {
        this.maxRequestsPerSecond = maxRequestPerSeconds;
        this.windowSizeInMilliseconds = windowSizeInMilliseconds;

    }

    //crate a synchornized method check if request is allowed or not
    public synchronized boolean tryAcquire(){
        long currentTime = System.currentTimeMillis();

        if(currentTime - windowStartTime > windowSizeInMilliseconds){
            windowStartTime = currentTime;
            requestCount = 0;
        }
        if(requestCount < maxRequestsPerSecond){
            requestCount++;
            return true;
        }

        return false;
        }

    
}
