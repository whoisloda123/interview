package com.liucan.kuroky.multthread;

import java.util.concurrent.*;

/**
 * 一.接口：ExecutorService，ScheduledExecutorService
 *  1.ThreadPoolExecutor是ExecutorService的实现者，就是线程池的实现
 *  2.ScheduledThreadPoolExecutor是ScheduledExecutorService的实现者， 能够将任务延后执行，或者间隔固定时间多次执行
 *  3.Executors可以很方便的创建ExecutorService和ScheduledExecutorService
 *
 * 二.ThreadPoolExecutor线程池里面的4种拒绝策略
 *  场景,当一个任务通过execute(Runnable)方法欲添加到线程池时：
 *      a.池中线程数小于corePoolSize，新任务都不排队而是直接添加新线程
 *      b.池中线程数大于等于corePoolSize，workQueue未满，首选将新任务加入workQueue而不是添加新线程
 *      c.池中线程数大于等于corePoolSize，workQueue已满，但是线程数小于maximumPoolSize，添加新的线程来处理被添加的任务
 *      d.池中线程数大于等于corePoolSize，workQueue已满，并且线程数大于等于maximumPoolSize，新任务被拒绝，使用handler处理被拒绝的任务
 *  也就是：处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务
 *  当线程池中的线程数量大于 corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数
 *
 *  1.AbortPolicy:抛出RejectedExecutionException异常：默认
 *  2.CallerRunsPolicy：由当前调用者的线程执行
 *  3.DiscardOldestPolicy：抛弃最早进入队列里面的任务
 *  4.DiscardPolicy：抛弃多余的任务
 *
 * 三.Executors提供了直接创建ThreadPoolExecutor线程池的快速方法，最经典的有4种
 * 参考：https://www.cnblogs.com/ruiati/p/6134131.html
 *
 *  1.newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
 *      线程池为无限大，60秒超时时间，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程
 *  2.newFixedThreadPool 创建一个定长线程池，0秒超时时间,可控制线程最大并发数，超出的线程会在队列中等待。
 *  3.newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。ScheduledExecutorService比Timer更安全，功能更强大
 *  4.newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
 *
 * 四.异常处理机制
 *  https://my.oschina.net/alvinlkk/blog/1925286
 *  1.execute会抛出异常，submit不会抛出
 *  2.创建线程池的时候可重写afterExecute,因在执行task的时候，会try-finally，在finally里面调用afterExecute
 * 五.这个线程池总结的好-必须看https://zhuanlan.zhihu.com/p/73990200
 * @author liucan
 * @version 19-3-4
 */
public class Executor1 {

    private void rejectExecutionPolicy(RejectedExecutionHandler rejectedExecutionHandler) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
                10,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10),
                rejectedExecutionHandler);

        for (int i = 0; i < 100; i++) {
            threadPoolExecutor.execute(() -> System.out.println(Thread.currentThread().getName() + " is running"));
        }

        threadPoolExecutor.shutdown();
    }

    public void test() {
        rejectExecutionPolicy(new ThreadPoolExecutor.AbortPolicy());
        rejectExecutionPolicy(new ThreadPoolExecutor.CallerRunsPolicy());
        rejectExecutionPolicy(new ThreadPoolExecutor.DiscardOldestPolicy());
        rejectExecutionPolicy(new ThreadPoolExecutor.DiscardPolicy());

        //不限制线程数（Integer.MAX_VALUE）,空闲线程60秒的过期时间
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        //定长线程池，不设置过期时间
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        //单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        //定长线程池，支持定时及周期性任务执行
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        try {
            executorService.submit(() -> "1").get();
            //并不会马上关闭，而是不接受新的任务，等所有正在执行的任务结束后，然后关闭
            executorService.shutdown();
            //马上关闭，即使有正在执行的任务，返回从未执行的任务列表
            executorService.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
