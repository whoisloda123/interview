package com.liucan.kuroky.multthread;

import lombok.Data;

import java.util.concurrent.Exchanger;

/**
 * Exchanger用于在两个线程之间进行数据交换，注意也只能在两个线程之间进行数据交换。线程会阻塞在Exchanger的exchange方法上，
 * 直到另外一个线程也到了同一个Exchanger的exchange方法时，二者进行数据交换，然后两个线程继续执行自身相关的代码。
 *
 * @author liucan
 * @version 19-3-9
 */
public class Exchanger1 {

    @Data
    private class ExchangerThread extends Thread {

        private String string;
        private Exchanger<String> exchanger;

        public ExchangerThread(Exchanger<String> exchanger, String string) {
            this.exchanger = exchanger;
            this.string = string;
        }

        @Override
        public void run() {
            try {
                string = exchanger.exchange(this.string);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void test() {
        Exchanger<String> exchanger = new Exchanger<>();
        ExchangerThread threadA = new ExchangerThread(exchanger, "a");
        ExchangerThread threadB = new ExchangerThread(exchanger, "b");

        threadA.start();
        threadB.start();

        System.out.println("threadA:" + threadA.getString());
        System.out.println("threadB:" + threadB.getString());

    }
}
