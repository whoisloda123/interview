package com.liucan.kuroky.multthread;

import java.util.concurrent.FutureTask;

/**
 * 参考：https://blog.csdn.net/hongtaolong/article/details/83349705
 * Callable产生结果，Future获取结果,2个一般配合使用
 * Future用于异步等待线程执行结果
 * FutureTask.get()是一个阻塞的方法，直到运行完成之后，返回线程执行的结果
 * set()方法会通知get方法会唤醒线程
 * FutureTask源码看下---------------------------------------------------------------
 *
 * @author liucan
 * @version 19-3-5
 */
public class Future1 {

    public void test() {
        //callable
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            Thread.sleep(2000);
            return "11111";
        });
        //FutureTask的run执行完成后会唤醒休眠的线程
        new Thread(futureTask).start();
        try {
            //线程休眠等待线程执行结果
            String s = futureTask.get();
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
