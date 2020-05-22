package com.weison.sbr.controller;


import com.weison.sbr.config.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@Slf4j
public class TaskController {

    private static volatile int NUMBER = 0;
    private static volatile int CORE_DATA_FLAG = 0;
    private static final Lock FAIR_LOCK = new ReentrantLock(true);
    private static final Lock NON_FAIR_LOCK = new ReentrantLock();

    @Resource
    RedissonClient redissonClient;

    @Resource
    DistributedLock distributedLock;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/locks/no")
    public String noLock() {
        NUMBER++;
        setThreadName("noLock" + NUMBER);
        log.info("==thread=={}==number=={}",getThread().getName(),NUMBER);
        return "OK";
    }

    @GetMapping("/locks/synchronized")
    public synchronized String synchronizedLock() {
        long begin = getTime();
        NUMBER++;
        setThreadName("synchronizedLock" + NUMBER);
        log.info("==thread=={}==number=={}",getThread().getName(),NUMBER);
        long end = getTime();
        log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    @GetMapping("/locks/reentrantLock")
    public String reentrantLock() throws InterruptedException {
        long begin = getTime();
        FAIR_LOCK.lock();
        try {
            NUMBER++;
            setThreadName("reentrantLock" + NUMBER);
            log.info("==thread=={}==number=={}",getThread().getName(),NUMBER);
        } finally {
            FAIR_LOCK.unlock();
        }
        long end = getTime();
        log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    @GetMapping("/locks/uf")
    public String rLockUnFairLock() throws InterruptedException {
        long begin = getTime();
        NON_FAIR_LOCK.tryLock(2, TimeUnit.SECONDS);
        try {
            NUMBER++;
            log.info("==thread=={}==number=={}",getThread().getName(),NUMBER);
            TimeUnit.SECONDS.sleep(3);
        } finally {
            NON_FAIR_LOCK.unlock();
        }
        long end = getTime();
        log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    @GetMapping("/locks/rl1")
    public String redisRLock1() throws InterruptedException {
        long begin = getTime();
        int number = 0;
        RLock lock = redissonClient.getFairLock("redisRLock1");
        boolean res = lock.tryLock(11, 11, TimeUnit.SECONDS);
        if (res) {
            try {
                NUMBER++;
                setThreadName("redisRLock1" + number);
                log.info("==thread=={}==number=={}",getThread().getName(),NUMBER);
                TimeUnit.SECONDS.sleep(1);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        long end = getTime();
        log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    private long getTime() {
        return System.currentTimeMillis();
    }

    @GetMapping("/locks/tryLock")
    public String redisRLock11() throws InterruptedException {
        long begin = getTime();
        RLock lock = redissonClient.getLock("myLock");
        lock.lock(10, TimeUnit.SECONDS);
        try {
            NUMBER++;
            setThreadName("redisRLock11" + NUMBER);
            log.info("==thread=={}==number=={}",getThread().getName(),NUMBER);
            TimeUnit.SECONDS.sleep(1);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        long end = getTime();
        log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    @GetMapping("/locks/tryLock/")
    public String redisRLock2() throws InterruptedException {
        long begin = getTime();
        RLock lock = redissonClient.getLock("myLock");
        boolean res = lock.tryLock(10, 10, TimeUnit.SECONDS);
        if (res) {
            try {
                NUMBER++;
                setThreadName("myLock" + NUMBER);
                log.info("==thread=={}==number=={}",getThread().getName(),NUMBER);
                TimeUnit.SECONDS.sleep(3);
                return String.valueOf(NUMBER);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        long end = getTime();
        log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }


    private void setThreadName(@RequestParam String name) {
        getThread().setName(name);
    }

    private Thread getThread() {
        return Thread.currentThread();
    }
}