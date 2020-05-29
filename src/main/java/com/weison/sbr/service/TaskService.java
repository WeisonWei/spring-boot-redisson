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
}
