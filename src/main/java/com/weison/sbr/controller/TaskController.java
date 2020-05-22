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
    public String noLock(@RequestParam String name) {
        NUMBER++;
        setThreadName(name + NUMBER);
        log.info("noLock-->{},  num-->{}", getThread().getName(), NUMBER);
        return "OK";
    }

    @GetMapping("/locks/synchronized")
    public synchronized String synchronizedLock(@RequestParam String name) {
        long begin = getTime();
        NUMBER++;
        setThreadName(name + NUMBER);
        log.info(getThread().getName() + "synchronized:" + NUMBER);
        long end = getTime();
        return NUMBER + "cost:" + (end - begin) + "ms";
    }

    @GetMapping("/locks/reentrantLock")
    public String reentrantLock(@RequestParam String name) throws InterruptedException {
        long begin = getTime();
        FAIR_LOCK.lock();
        try {
            NUMBER++;
            setThreadName(name + NUMBER);
            log.info(getThread().getName() + "rLockFairLock:" + NUMBER);
        } finally {
            FAIR_LOCK.unlock();
        }
        long end = getTime();
        return NUMBER + "cost:" + (end - begin) + "ms";
    }

    @GetMapping("/locks/uf")
    public String rLockUnFairLock(@RequestParam String name) throws InterruptedException {
        long begin = getTime();
        NON_FAIR_LOCK.tryLock(2, TimeUnit.SECONDS);
        try {
            NUMBER++;
            setThreadName(name + NUMBER);
            TimeUnit.SECONDS.sleep(3);
        } finally {
            NON_FAIR_LOCK.unlock();
        }
        long end = getTime();
        log.info("rLockUnFairLock:" + getThread().getName() + "  num-->" + NUMBER + "  flag-->" + CORE_DATA_FLAG);
        return NUMBER + "cost:" + (end - begin) + "ms";
    }

    @GetMapping("/locks/rl1")
    public String redisRLock1(@RequestParam String name) throws InterruptedException {
        long begin = getTime();
        int number = 0;
        RLock lock = redissonClient.getFairLock(name);
        boolean res = lock.tryLock(11, 11, TimeUnit.SECONDS);
        if (res) {
            try {
                NUMBER++;
                setThreadName(name + number);
                log.info(getThread().getName() + "redisRLock1:" + number);
                TimeUnit.SECONDS.sleep(1);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        long end = getTime();
        log.info(number + "cost:" + (end - begin) + "ms");
        return number + "cost:" + (end - begin) + "ms";
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
            setThreadName("myLock" + NUMBER);
            log.info(getThread().getName() + "redisRLock1:" + NUMBER);
            TimeUnit.SECONDS.sleep(1);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        long end = getTime();
        String result = NUMBER + "cost:" + (end - begin) + "ms";
        log.info("==over=={}", result);
        return result;
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
                log.info(getThread().getName() + "redisRLock2:" + NUMBER);
                TimeUnit.SECONDS.sleep(3);
                return String.valueOf(NUMBER);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        long end = getTime();
        String result = NUMBER + "cost:" + (end - begin) + "ms";
        log.info("==over=={}", result);
        return NUMBER + "cost:" + (end - begin) + "ms";
    }


    private void setThreadName(@RequestParam String name) {
        getThread().setName(name);
    }

    private Thread getThread() {
        return Thread.currentThread();
    }
}
