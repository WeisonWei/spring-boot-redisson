package com.weison.sbr.config;

import java.util.function.Supplier;

public interface DistributedLock {

    <T> T lockAndExec(String lockKey, Supplier<T> supplier);

    <T> T fairLockAndExec(String lockKey, Supplier<T> supplier);
}
