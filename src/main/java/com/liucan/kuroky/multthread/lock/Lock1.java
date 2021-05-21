package com.liucan.kuroky.multthread.lock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.*;

/**
 * @author liucan
 * @version 19-3-5
 */
public class Lock1 {
    //可重入锁
    private final Lock reentrantLock = new ReentrantLock();
    //条件变量
    private final Condition condition = reentrantLock.newCondition();
    //读写锁
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    //自旋锁
    private final Lock spinLock = new SpinLock();
    private int i = 0;

    private void spinLockTest() {
        //spinLock和
    }

    private void readWriteLockTest() {
        Queue<Integer> integers = new LinkedList<>();
        integers.add(1);
        for (int j = 0; j < 2; j++) {
            new Thread(() -> {
                try {
                    readLock.lock(); //可同时读
                    integers.peek();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    readLock.unlock();
                }
            }).start();
        }
    }

    private void lockTest() {
        new Thread(() -> {
            try {
                reentrantLock.lock();
                i++;
                while (i != 2) { //防止假唤醒
                    condition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        }).start();

        new Thread(() -> {
            try {
                reentrantLock.lock();
                i++;
                condition.signal();
            } finally {
                reentrantLock.unlock();
            }
        }).start();
    }

    public void test() {
        lockTest();
        readWriteLockTest();
        spinLockTest();
    }
}
