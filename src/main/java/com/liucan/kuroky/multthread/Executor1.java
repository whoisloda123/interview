package com.liucan.kuroky.multthread;

import java.util.concurrent.*;

/**
 * 一.源码原理：https://cloud.tencent.com/developer/article/1124439
 *
 * 二.工作原理
 *  当一个任务通过execute(Runnable,submit最后也是调用这个)方法欲添加到线程池时：
 *    a.池中线程数小于corePoolSize，直接创建线程执行任务
 *    b.否则往队列里面添加
 *    c.队列已满，直接创建线程执行任务
 *    d.线程数大于等于maximumPoolSize，执行拒绝策略（抛异常，调用者执行，抛弃最早线程，直接抛弃）
 *  1.当线程池中的线程数量大于 corePoolSize时，如果非核心线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数
 *  2.ctl变量，包含线程池允许状态runStatus（running,shutdown,stop,eg）和workCount
 *  3.work线程执行初始任务后，一直从队列里面取数据直到为null,销毁线程，取的时候
 *    a.runStatus为stop或shutdown且队列为空返回null
 *    b.workCount大于核心线程 poll（keepAliveTime）阻塞获取（这种方式实现线程收回），获取不到返回null，take死循环阻塞获取
 *  4.获取到任务执行run的方法，会调用可扩展的beforeExecute和afterExecute方法,可在扩展方法里面加入如：暂停所有线程，统计线程执行时间等
 *  5.销毁线程的时候，会尝试调用一下terminated（线程池结束的时候）扩展方法
 *  6.shutdown中断所有线程，runStatus为shutdown，不能新添加线程，会继续执行任务，shutdownNow中断所有线程发送中断信号，runStatus为shutdown，不能新添加线程和执行任务

 * 三.Executors提供了直接创建ThreadPoolExecutor线程池的快速方法，最经典的有4种
 *  1.newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
 *      核心线程为0，线程池为无限大，同步队列，60秒超时时间，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程
 *  2.newFixedThreadPool 创建一个定长线程池，0秒超时时间,可控制线程最大并发数，超出的线程会在队列中等待。
 *  3.newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。ScheduledExecutorService比Timer更安全，功能更强大
 *  4.newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
 *
 * 四.异常处理机制
 *  https://my.oschina.net/alvinlkk/blog/1925286
 *  1.submit不会抛出异常（FutureTask将异常拦截了），通过返回的FutureTask.get获取，execute会
 *  2.创建线程池的时候可重写afterExecute,因在执行task的时候，会try-finally，在finally里面调用afterExecute
 *  3.可以设置thread的UncaughtExceptionHandler
 *
 * 六.ScheduledThreadPoolExecutor
 * https://zhuanlan.zhihu.com/p/214234400
 *  1.基于ThreadPoolExecutor来实现的
 *  2.重写FutureTask的run方法，在执行完任务后，计算下一次执行时，从写将当前任务放入队列中
 *  3.队列是延时队列，基于小顶堆来排序
 *  4.scheduleAtFixedRate以固定的间隔来执行任务，如果时间到了上个没有执行完成，会等上一个任务执行完，
 *  5.scheduleWithFixedDelay以固定延迟来执行，等上个任务执行完后，隔多少时间执行
 *
 * 六.CompletableFuture
 *  1.可以很好的处理多个任务，如果等待2个任务执行完，做后续处理
 *  https://blog.csdn.net/w306026355/article/details/109707269
 *
 * 七.ForkJoinPoll
 * https://cloud.tencent.com/developer/article/1704658
 *  1.采用分治法，一个任务可以划分为多个任务，然后将多个任务分别执行的结果合并
 *  2.每个任务有有个队列，空闲线程会去其他任务队列里面取任务来执行
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

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> submit = forkJoinPool.submit(new CalculateTask(1, 1000));
        try {
            Integer integer = submit.get();
            System.out.println(integer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static class CalculateTask extends RecursiveTask<Integer> {
        private static final long serialVersionUID = 1L;
        private static final int THRESHOLD = 49;
        private final int start;
        private final int end;

        public CalculateTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            if (end - start <= THRESHOLD) {
                int result = 0;
                for (int i = start; i <= end; i++) {
                    result += i;
                }
                return result;
            } else {
                int middle = (start + end) / 2;
                CalculateTask firstTask = new CalculateTask(start, middle);
                CalculateTask secondTask = new CalculateTask(middle + 1, end);
                invokeAll(firstTask,secondTask);
                return firstTask.join() + secondTask.join();
            }
        }
    }
}
