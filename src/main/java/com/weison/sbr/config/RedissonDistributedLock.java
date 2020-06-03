package com.weison.sbr.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedissonDistributedLock implements DistributedLock {

    private RedissonClient redissonClient;

    public <T> T lockAndExec(String lockKey, Supplier<T> supplier) {
        String threadName = Thread.currentThread().getName();
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            rLock.lock(10, TimeUnit.SECONDS);
            log.debug("ThreadName:{}--获取lock:{}成功", threadName, lockKey);
            T t = supplier.get();
            log.debug("==lock==result={}", t);
            return t;
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.debug("ThreadName:{}--释放lock:{}", threadName, lockKey);
            }
        }
    }

    @Override
    public <T> T fairLockAndExec(String lockKey, Supplier<T> supplier) {
        String threadName = Thread.currentThread().getName();
        RLock rLock = redissonClient.getFairLock(lockKey);
        try {
            rLock.lock(10, TimeUnit.SECONDS);
            log.debug("ThreadName:{}--获取lock:{}成功", threadName, lockKey);
            T t = supplier.get();
            log.debug("==lock==result={}", t);
            return t;
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.debug("ThreadName:{}--释放lock:{}", threadName, lockKey);
            }
        }
    }
}


