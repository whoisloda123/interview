package com.liucan.kuroky.multthread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 自旋锁
 *
 * @author liucan
 * @version 19-2-26
 */
public class SpinLock implements Lock {

    //原子锁
    private final AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);

    @Override
    public void lock() {
        while (!tryLock()) ;
    }

    /**
     * 处理中断由线程本身自己处理，而调用Thread.Interrupted只是设置一下状态位
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        lock();
    }

    @Override
    public boolean tryLock() {
        return atomicReference.compareAndSet(false, true);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        long l = System.currentTimeMillis();
        while (!tryLock()) {
            if (System.currentTimeMillis() > l + unit.toMillis(time)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void unlock() {
        atomicReference.set(false);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
