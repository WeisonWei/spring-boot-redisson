package com.weison.sbr.service;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
public interface TaskService {

    String fairReEntrantLock();

    String dir(String key);

    String noLock();

    String rLock() throws InterruptedException;

    String rLockExec();

    String reEntrantLock() throws InterruptedException;

    String synchronizedLock();

    String trLock() throws InterruptedException;

    String trfLock() throws InterruptedException;

    Float getAmount();

    Float useAmount();
}
