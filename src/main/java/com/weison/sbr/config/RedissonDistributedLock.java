package com.weison.sbr.config;


import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


@Slf4j
public class RedissonDistributedLock implements DistributedLock {

    private RedissonClient redissonClient;

    @Override
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    @Override
    public RLock lock(String lockKey, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    @Override
    public RLock lock(String lockKey, TimeUnit unit, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    @Override
    public void unlock(RLock lock) {
        lock.unlock();
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <T> T lockAndExec(Long userId, String lockFlag, Supplier<T> supplier) {
        String threadName = Thread.currentThread().getName();
        String lf = userId + lockFlag;
        //RLock rLock = lock(lf);
        RLock rLock = getFairLock(userId, lockFlag);
        T result = null;
        try {
            //20s获取不到锁认为获取锁失败 10s锁失效
            boolean isGetLock = rLock.tryLock(10, 10, TimeUnit.SECONDS);
            log.debug("ThreadName:{}==获取lock:{}成功", threadName, userId + lockFlag);
            if (isGetLock) {
                log.debug("ThreadName:{}==上锁lock:{}成功", threadName, userId + lockFlag);
                result = supplier.get();
                log.debug("==lock==result=={}", result);
            }
        } catch (InterruptedException e) {
            log.debug("获取lock:{}失败", userId + lockFlag);
            throw new RuntimeException(e);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.debug("释放lock:{}", userId + lockFlag);
            }
        }
        log.debug("==lock==result=r={}", result);
        return result;
    }

    @Override
    public <T> T fairLockAndExec(Long userId, String lockFlag, Supplier<T> supplier) {
        RLock rLock = getFairLock(userId, lockFlag);
        T result = null;
        try {
            //20s获取不到锁认为获取锁失败 10s锁失效
            boolean isGetLock = rLock.tryLock(10, 10, TimeUnit.SECONDS);
            log.debug("获取fairLock:{}成功", userId + lockFlag);
            if (isGetLock) {
                result = supplier.get();
                log.debug("==fairLock==result=={}", result);
            }
        } catch (InterruptedException e) {
            log.debug("获取fairLock:{}失败", userId + lockFlag);
            throw new RuntimeException(e);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.debug("释放fairLock:{}", userId + lockFlag);
            }
        }
        log.debug("==fairLock==result=r={}", result);
        return result;
    }

    /**
     * 锁格式: "userId_业务标示"
     *
     * @param userId
     * @param lockFlag
     * @return
     */
    public RLock getLock(Long userId, String lockFlag) {
        if (Objects.isNull(userId)) {
            throw new RuntimeException("userId为空");
        }
        if (Objects.isNull(lockFlag)) {
            throw new RuntimeException("lockFlag为空");
        }
        String lockKey = userId.toString() + lockFlag;
        return redissonClient.getLock(lockKey);
    }

    /**
     * 锁格式: "userId_业务标示"
     *
     * @param userId
     * @param lockFlag
     * @return
     */
    public RLock getFairLock(Long userId, String lockFlag) {
        if (Objects.isNull(userId)) {
            throw new RuntimeException("userId为空");
        }
        if (Objects.isNull(lockFlag)) {
            throw new RuntimeException("lockFlag为空");
        }
        String lockKey = userId.toString() + lockFlag;
        return redissonClient.getFairLock(lockKey);
    }
}

