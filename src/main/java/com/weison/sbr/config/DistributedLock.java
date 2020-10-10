package com.weison.sbr.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
public interface DistributedLock {

    <T> T lockAndExec(String lockKey, Supplier<T> supplier);

    <T> void lockAndExec(String lockKey, T t, Consumer<T> consumer);

    <T> T lockAndExecOnce(String lockKey, Supplier<T> supplier);

    <T> T fairLockAndExec(String lockKey, Supplier<T> supplier);

    <T> T multiLockAndExec(String lockKey, String typeLockKey, Supplier<T> supplier);

}
