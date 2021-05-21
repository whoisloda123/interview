package com.liucan.kuroky.multthread;

import java.util.concurrent.Semaphore;

/**
 * Semaphore可以控制某个资源或代码块可被同时访问的个数
 *  主要用于控制并发数
 *  通过Semaphore控制并发并发数的方式和通过控制线程数来控制并发数的方式相比，粒度更小，因为Semaphore可以通过acquire方法和release方法来控制代码块的并发数
 * @author liucan
 * @version 19-3-4
 */
public class Semaphore1 {

    public void test() {
        String[] strings = {"1", "2", "3"};
        Semaphore semaphore = new Semaphore(2);
        new Thread(() -> {
            try {
                semaphore.acquire();
                Thread.sleep(1000);
                System.out.println(strings[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            semaphore.release();
        }).start();

        new Thread(() -> {
            try {
                semaphore.acquire();
                Thread.sleep(1000);
                System.out.println(strings[1]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            semaphore.release();
        }).start();

        new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println(strings[2]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            semaphore.release();
        }).start();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
