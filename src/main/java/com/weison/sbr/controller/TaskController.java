package com.weison.sbr.controller;

import com.weison.sbr.config.DistributedLock;
import com.weison.sbr.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Resource
    private TaskService taskService;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/locks/no")
    public String noLock() {
        NUMBER++;
        setThreadName("noLock" + NUMBER);
        log.info("==thread=={}==number=={}", getThread().getName(), NUMBER);
        return "OK";
    }

    @GetMapping("/locks/synchronized")
    public synchronized String synchronizedLock() {
        long begin = getTime();
        NUMBER++;
        setThreadName("synchronizedLock" + NUMBER);
        log.info("==thread=={}==number=={}", getThread().getName(), NUMBER);
        long end = getTime();
        log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    @GetMapping("/locks/re-entrant-lock-f")
    public String fairReEntrantLock() throws InterruptedException {
        long begin = getTime();
        FAIR_LOCK.lock();
        try {
            NUMBER++;
            setThreadName("reentrantLock" + NUMBER);
            log.info("==thread=={}==number=={}", getThread().getName(), NUMBER);
        } finally {
            FAIR_LOCK.unlock();
        }
        long end = getTime();
        log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }


    @GetMapping("/locks/e-entrant-lock")
    public String reEntrantLock() throws InterruptedException {
        long begin = getTime();
        boolean b = NON_FAIR_LOCK.tryLock(2, TimeUnit.SECONDS);
        if (b) {
            try {
                NUMBER++;
                log.info("==thread=={}==number=={}", getThread().getName(), NUMBER);
                TimeUnit.SECONDS.sleep(3);
            } finally {

                NON_FAIR_LOCK.unlock();
            }
            long end = getTime();
            log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
            return "OK";
        }
        return "NO";
    }


    @GetMapping("/locks/r-lock")
    public String rLock() throws InterruptedException {
        long begin = getTime();
        RLock lock = redissonClient.getLock("lock");
        lock.lock(10, TimeUnit.SECONDS);
        try {
            NUMBER++;
            setThreadName("redisRLock11" + NUMBER);
            log.info("==thread=={}==number=={}", getThread().getName(), NUMBER);
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


    @GetMapping("/locks/rtf-lock")
    public String trfLock() throws InterruptedException {
        long begin = getTime();
        int number = 0;
        RLock lock = redissonClient.getFairLock("rl1");
        boolean res = lock.tryLock(10, 10, TimeUnit.SECONDS);
        if (res) {
            try {
                NUMBER++;
                setThreadName("redisRLock1" + number);
                log.info("==thread=={}==number=={}", getThread().getName(), NUMBER);
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


    /**
     * 线程 或 lambda 如果有共享资源竞争 则锁无效
     *
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/locks/rt-lock")
    public String trLock() throws InterruptedException {
        long begin = getTime();
        RLock lock = redissonClient.getLock("tryLock");
        boolean res = lock.tryLock(10, 10, TimeUnit.SECONDS);
        if (res) {
            try {
                NUMBER++;
                setThreadName("myLock" + NUMBER);
                new Thread(() -> {
                    try {
                        log.info("==thread=={}==number=={}", getThread().getName(), NUMBER);
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
        log.debug("==thread=={}==cost=={}", getThread().getName(), (end - begin) / 1000);
        return "OK";
    }

    /**
     * lambda无效 因为只是触发
     *
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/locks/r-lock-e")
    public String rLockExec() throws InterruptedException {
        String s = distributedLock.lockAndExec(1l, "12345", () -> {
            NUMBER++;
            log.info("==thread=={}==number=={}", getThread().getName(), NUMBER);
            return "OK";
        });

        return s;
    }


    private void setThreadName(@RequestParam String name) {
        getThread().setName(name);
    }

    private Thread getThread() {
        return Thread.currentThread();
    }

    private long getTime() {
        return System.currentTimeMillis();
    }


}
