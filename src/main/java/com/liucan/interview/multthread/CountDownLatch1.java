package com.liucan.interview.multthread;

import java.util.concurrent.CountDownLatch;

/**
 * 允许一个或多个线程等待一系列指定操作的完成
 *
 * @author liucan
 * @version 19-3-3
 */
public class CountDownLatch1 {

    private void test() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        countDownLatch.countDown();
    }
}
