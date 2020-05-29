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
        log.info("--noLock--over--");
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
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBER);

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
        log.info("--noLock--over--");
    }
}