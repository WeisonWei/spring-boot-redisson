package com.weison.sbr.config;


import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface DistributedLock {

    RLock lock(String lockKey);

    RLock lock(String lockKey, int timeout);

    RLock lock(String lockKey, TimeUnit unit, int timeout);

    boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime);

    void unlock(String lockKey);

    void unlock(RLock lock);

    <T> T lockAndExec(Long userId, String lockFlag, Supplier<T> supplier);

    <T> T fairLockAndExec(Long userId, String lockFlag, Supplier<T> supplier);
}
