package com.weison.sbr.service;

import com.weison.sbr.config.DistributedLock;
import com.weison.sbr.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private static final Lock FAIR_LOCK = new ReentrantLock(true);
    private static final Lock NON_FAIR_LOCK = new ReentrantLock();
    private static volatile int NUMBER = 0;

    @Resource
    private TaskRepository TaskRepository;

    @Resource
    private DistributedLock distributedLock;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 无锁出现线程安全问题
     *
     * @return
     */
    public String noLock() {
        NUMBER++;
        log.info("--thread={}--number={}", getThread().getName(), NUMBER);
        return "OK";
    }

    /**
     * 线程安全 但并不一定公平
     *
     * @return
     */
    public String fairReEntrantLock() {
        long begin = getTime();
        FAIR_LOCK.lock();
        try {
            log.info("--thread={}--number={}", getThread().getName(), NUMBER);
            NUMBER++;
        } finally {
            log.info("--thread={}--unlock", getThread().getName());
            FAIR_LOCK.unlock();
        }
        long end = getTime();
        log.debug("--thread={}--cost={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    @Override
    public String dir(String key) {
        distributedLock.lockAndExec("order:refund:" + key + "_refund", key, System.out::print);
        return key;
    }

    /**
     * tryLock等待时长较短 则部分线程竞争不到
     * 本例中 10s部分不再等待 30s则可以跑完
     *
     * @return
     * @throws InterruptedException
     */
    public String reEntrantLock() throws InterruptedException {
        long begin = getTime();
        boolean b = NON_FAIR_LOCK.tryLock(20, TimeUnit.SECONDS);
        if (b) {
            try {
                NUMBER++;
                log.info("--thread={}--number={}", getThread().getName(), NUMBER);
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.info("--thread={}--unlock", getThread().getName());
                NON_FAIR_LOCK.unlock();
            }
            long end = getTime();
            log.debug("--thread={}--cost={}", getThread().getName(), (end - begin) / 1000);
            return "OK";
        }
        log.info("--thread={}--NO", getThread().getName());
        return "NO";
    }

    public synchronized String synchronizedLock() {
        long begin = getTime();
        NUMBER++;
        log.info("--thread={}--number={}", getThread().getName(), NUMBER);
        long end = getTime();
        log.debug("--thread={}--cost={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    public String rLock() throws InterruptedException {
        long begin = getTime();
        RLock lock = redissonClient.getLock("lock");
        lock.lock(10, TimeUnit.SECONDS);
        try {
            NUMBER++;
            log.info("--thread={}--number={}", getThread().getName(), NUMBER);
            TimeUnit.SECONDS.sleep(1);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        long end = getTime();
        log.debug("--thread={}--cost={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    /**
     * lambda无效 因为只是触发
     *
     * @return
     * @throws InterruptedException
     */
    public String rLockExec() {
        String s = distributedLock.lockAndExec("12345", () -> {
            NUMBER++;
            log.info("--thread={}--number={}", getThread().getName(), NUMBER);
            return "OK";
        });

        return s;
    }

    /**
     * 线程 或 lambda 如果有共享资源竞争 则锁无效
     *
     * @return
     * @throws InterruptedException
     */
    public String trLock() throws InterruptedException {
        long begin = getTime();
        RLock lock = redissonClient.getLock("tryLock");
        boolean res = lock.tryLock(10, 10, TimeUnit.SECONDS);
        if (res) {
            try {
                NUMBER++;
                new Thread(() -> {
                    try {
                        log.info("--thread={}--number={}", getThread().getName(), NUMBER);
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                return String.valueOf(NUMBER);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        long end = getTime();
        log.debug("--thread={}--cost={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    public String trfLock() throws InterruptedException {
        long begin = getTime();
        int number = 0;
        RLock lock = redissonClient.getFairLock("rl1");
        boolean res = lock.tryLock(50, 10, TimeUnit.SECONDS);
        if (res) {
            try {
                NUMBER++;
                log.info("--thread={}--number={}", getThread().getName(), NUMBER);
                TimeUnit.SECONDS.sleep(1);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        long end = getTime();
        log.debug("--thread={}--cost={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    @Override
    public Float getAmount() {
        Double random = Math.random();
        float amount = random.floatValue();
        RBucket<Object> redisAmount = redissonClient.getBucket("amount");
        if (redisAmount.isExists()) {
            return (Float) redisAmount.get();
        } else {
            redisAmount.set(amount);
        }
        return amount;
    }

    @Override
    public Float useAmount() {
        RBucket<Object> redisAmount = redissonClient.getBucket("amount");
        if (redisAmount.isExists()) {
            return (Float) redisAmount.getAndDelete();
        }
        return null;
    }

    private long getTime() {
        return System.currentTimeMillis();
    }

    private Thread getThread() {
        return Thread.currentThread();
    }

}
