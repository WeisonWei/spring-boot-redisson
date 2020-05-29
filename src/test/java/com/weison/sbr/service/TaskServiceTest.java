package com.weison.sbr.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.*;


@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@Rollback
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskServiceTest {

    private static final int THREAD_NUMBER = 10;


    @Autowired
    private TaskService taskService;

    @Test
    @DisplayName("无锁调用")
    @Order(1)
    public void noLock() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(THREAD_NUMBER);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUMBER);
        log.info("--noLock--start--");
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        System.out.println("--系统一个有--" + cpuNumber + "个cpu--");
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBER);

        for (int i = 0; i < THREAD_NUMBER; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    log.info(finalI + " 到达栅栏");
                    barrier.await();
                    log.info(finalI + " 冲破栅栏");
                    String result = taskService.noLock();
                    log.info(finalI + " -- " + result);
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        log.info("--noLock--over--");
    }

    @Test
    @DisplayName("可重入公平锁调用")
    @Order(2)
    public void fairReEntrantLock() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(THREAD_NUMBER);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUMBER);
        log.info("--fairReEntrantLock--start--");
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        System.out.println("--系统一个有--" + cpuNumber + "个cpu--");
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBER);

        for (int i = 0; i < THREAD_NUMBER; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    log.info(finalI + " = 到达栅栏 =");
                    barrier.await();
                    log.info(finalI + " - 冲破栅栏 -");
                    taskService.fairReEntrantLock();
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        log.info("--fairReEntrantLock--over--");
    }

    @Test
    @DisplayName("可重入锁调用")
    @Order(3)
    public void reEntrantLock() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(THREAD_NUMBER);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUMBER);
        log.info("--reEntrantLock--start--");
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        System.out.println("--系统一个有--" + cpuNumber + "个cpu--");
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < THREAD_NUMBER; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    log.info(finalI + " = 到达栅栏 =");
                    barrier.await();
                    log.info(finalI + " - 冲破栅栏 -");
                    taskService.reEntrantLock();
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        log.info("--reEntrantLock--over--");
    }

    @Test
    @DisplayName("synchronized调用")
    @Order(4)
    public void synchronizedLock() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(THREAD_NUMBER);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUMBER);
        log.info("--synchronizedLock--start--");
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        System.out.println("--系统一个有--" + cpuNumber + "个cpu--");
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < THREAD_NUMBER; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    log.info(finalI + " = 到达栅栏 =");
                    barrier.await();
                    log.info(finalI + " - 冲破栅栏 -");
                    taskService.synchronizedLock();
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        log.info("--synchronizedLock--over--");
    }

    @Test
    @DisplayName("RLock调用")
    @Order(4)
    public void rLock() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(THREAD_NUMBER);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUMBER);
        log.info("--rLock--start--");
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        System.out.println("--系统一个有--" + cpuNumber + "个cpu--");
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < THREAD_NUMBER; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    log.info(finalI + " = 到达栅栏 =");
                    barrier.await();
                    log.info(finalI + " - 冲破栅栏 -");
                    taskService.rLock();
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        log.info("--rLock--over--");
    }

    @Test
    @DisplayName("rLockExec调用")
    @Order(5)
    public void rLockExec() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(30);
        CountDownLatch countDownLatch = new CountDownLatch(30);
        log.info("--rLockExec--start--");
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        System.out.println("--系统一个有--" + cpuNumber + "个cpu--");
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < 30; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    log.info(finalI + " = 到达栅栏 =");
                    barrier.await();
                    log.info(finalI + " - 冲破栅栏 -");
                    taskService.rLockExec();
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        log.info("--rLockExec--over--");
    }

    @Test
    @DisplayName("trLock调用")
    @Order(6)
    public void trLock() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(30);
        CountDownLatch countDownLatch = new CountDownLatch(30);
        log.info("--trLock--start--");
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        System.out.println("--系统一个有--" + cpuNumber + "个cpu--");
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < 30; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    log.info(finalI + " = 到达栅栏 =");
                    barrier.await();
                    log.info(finalI + " - 冲破栅栏 -");
                    taskService.trLock();
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        log.info("--trLock--over--");
    }

    @Test
    @DisplayName("trfLock调用")
    @Order(7)
    public void trfLock() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(30);
        CountDownLatch countDownLatch = new CountDownLatch(30);
        log.info("--trfLock--start--");
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        System.out.println("--系统一个有--" + cpuNumber + "个cpu--");
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < 30; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    log.info(finalI + " = 到达栅栏 =");
                    barrier.await();
                    log.info(finalI + " - 冲破栅栏 -");
                    taskService.trfLock();
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        log.info("--trfLock--over--");
    }
}