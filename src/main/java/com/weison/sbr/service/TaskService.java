package com.weison.sbr.service;

public interface TaskService {

    String fairReEntrantLock();

    String noLock();

    String rLock() throws InterruptedException;

    String rLockExec();

    String reEntrantLock() throws InterruptedException;

    String synchronizedLock();

    String trLock() throws InterruptedException;

    String trfLock() throws InterruptedException;

    /**
     * 获取奖励金额 每次覆盖之前
     *
     * @return
     */
    Float getAmount();

    /**
     * 使用奖励金额 使用后清除
     *
     * @return
     */
    Float useAmount();
}
