package com.weison.sbr.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedissonLock implements DistributedLock {

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
    public <T> void lockAndExec(String lockKey, T t, Consumer<T> consumer) {
        String threadName = Thread.currentThread().getName();
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            rLock.lock(10, TimeUnit.SECONDS);
            log.debug("ThreadName:{}--获取lock:{}成功", threadName, lockKey);
            consumer.accept(t);
            log.debug("==lock==exec==end");
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.debug("ThreadName:{}--释放lock:{}", threadName, lockKey);
            }
        }
    }

    public <T> T lockAndExecOnce(String lockKey, Supplier<T> supplier) {
        String threadName = Thread.currentThread().getName();
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = rLock.tryLock();
            if (isLocked) {
                log.debug("ThreadName:{}--获取lock:{}成功", threadName, lockKey);
                T t = supplier.get();
                log.debug("==lock==result={}", t);
                return t;
            } else {
                return null;
            }
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.debug("ThreadName:{}--释放lock:{}", threadName, lockKey);
            }
        }
    }

    @Override
    public <T> T multiLockAndExec(String lockKey, String typeLockKey, Supplier<T> supplier) {
        String threadName = Thread.currentThread().getName();
        RLock rLock = redissonClient.getLock(lockKey);
        RLock accountTypeLock = redissonClient.getLock(typeLockKey);
        RedissonMultiLock multiLock = new RedissonMultiLock(rLock, accountTypeLock);
        try {
            multiLock.lock(10, TimeUnit.SECONDS);
            log.debug("ThreadName:{}--获取lock:{}成功", threadName, lockKey);
            T t = supplier.get();
            log.debug("==lock==result={}", t);
            return t;
        } finally {
            if (multiLock.isLocked() && multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
                log.debug("ThreadName:{}--释放lock1:{},lock2:{}", threadName, lockKey, typeLockKey);
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


