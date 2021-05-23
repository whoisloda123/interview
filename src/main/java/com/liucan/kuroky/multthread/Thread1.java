package com.liucan.kuroky.multthread;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* *
 *
 *
 * 四.volatile
 * a.多线程同时访问一个对象时（主内存），每个执行的线程可以拥有该对象的一份拷贝（本地内存），而本地内存的操作会在一个时机(线程执行完毕)同步到主内存
 * 所以程序在执行过程中，一个线程看到的变量并不一定是最新的
 * b.轻量级锁
 * c.能保证共享变量对所有线程的可见性，当写一个volatile变量时，JMM会把该线程对应的本地内存中的变量强制刷新到主内存中去
 * 保证所有线程对该变量的可见性
 * d.禁止指令重排序优化:
 *      1.重排序优化：指编译器和处理器为了优化程序性能而对指令序列进行排序的一种手段
 *          可能会对代码的执行顺序重新排序，但是不会影响结果，单线程不会出现问题，但是多线程就无法保证了
 *      2.volatile则能按照一定的规律阻止指令重排序优化
 * e.volatile对于单个的共享变量的读/写具有原子性，但是像num++这种复合操作，volatile无法保证其原子性,可以用原子锁
 *
 * 五.synchronized
 *  1.synchronized锁定是一个对象，其他试图访问该对象synchronized方法或代码块会被锁住,而每一个对象都可以做为一个锁（Monitor锁）
 *  2.在普通方法前面，锁的是当前实例对象（其他的synchronized标志的方法也会被锁住,非synchronized的不会被锁住）
 *  3.在静态方法前面，锁的是整个类
 *  4.在方法块里面synchronized(object),锁的是括号里面的对象
 *  5.实现机制
 *      a.每个对象有个监视器锁（Monitor锁）
 *      b.Monitor被占用的时候其他线程会阻塞，进入执行命令MonitorEnter获取Monitor,退出执行MonitorExit释放Monitor
 *      c.notify/notifyAll和wait方法都依赖Monitor锁
 *      d.synchronized方法，和方法块是基本Monitor锁实现，执行时候进入获取锁，离开释放锁
 *      e.所以notify/notifyAll和wait方法都必须位于synchronized内，否者抛异常
 *      f.wait()方法立即释放对象监视器，notify()/notifyAll()方法则会等待线程剩余代码执行完毕才会放弃对象监视器
 *
 * 六.线程同步
 *  2.sleep,yield,wait区别
 *      a.sleep后，不会释放当前锁，会释放cpu时间片，暂停线程，时间到线程处于可以调用状态
 *      b.yield后，不会释放当前锁，会释放cpu时间片，线程处于可以调度状态（ps：可能出现yield后，马上又被调用，完全取决于线程调度器）
 *      c.wait后，会释放当前锁，会释放cpu时间片，暂停当前线程，直到被notify/notifyAll通知
 *  4.ThreadLocal
 *      1.每个线程独有一份，和TreadLocal在哪个地方和有多少个对象没有关系，和里面的map有关系，ThreadLocal里面是保存的是map（当前线程对应key，对应value）
 *      2.想保存多个本地线程数据，就定义多个TreadLocal，因里面都是和Thread.currentThread操作有关系
 * 七.锁分类
 *      a.公平锁/非公平锁：是否按照申请的顺序来获得锁,通过ReentrantLock构造函数来
 *          1.ReentrantLock构造函数来制定
 *          2.synchronized是非公平锁
 *      b.可重入锁（递归锁）:可多次加锁，ReentrantLock和synchronized都是
 *      c.独享锁/共享锁(互斥锁/读写锁):读写锁，用ReentrantReadWriteLock，读锁共享，写锁互斥
 *      d.乐观锁/悲观锁
 *          1.悲观锁认为对于同一个数据操作其他线程会修改，一定要加锁：常用锁
 *          2.乐观锁认为对于同一个数据操作其他线程不会修改，不需要加锁：用自旋锁
 *      e.偏向锁/轻量级锁/重量级锁:指的是锁的状态，是针对synchronized的---synchronized锁的优化
 *      参考：https://www.jianshu.com/p/36eedeb3f912
 *          1.偏向锁:一段代码一直被一个线程所访问，该线程会自动获取锁（有个获取锁的过程）。降低获取锁的代价,无实际竞争，且将来只有第一个申请锁的线程会使用锁
 *          2.轻量级锁:指当锁是偏向锁的时候，被另一个线程所访问，偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，提高性能。
 *          3.重量级锁是指当锁为轻量级锁的时候，另一个线程虽然是自旋，但自旋不会一直持续下去，当自旋一定次数的时候，还没有获取到锁，就会进入阻塞，
 *              该锁膨胀为重量级锁。重量级锁会让其他申请的线程进入阻塞，性能降低
 *      e.自旋锁（无锁）:线程不会阻塞，不会释放cpu时间片，而一直循环等待，采用原子锁cas（compare and swap）方式
 *
 *  八.CAS和AQS
 *     1.CAS(compare and swap)，原子操作
 *        a.UnSafe类提供了硬件级别的原子操作，一般AtomicInteger等原子类都做了封装
 *     2.AQS(AbstractQueuedSynchronizer)
 *        a.是ReentrantLock、Semaphore，CountDownLatch等线程同步的基类，是构建锁和同步器的框架
 *        b.通过一个volatile的status状态变量和FIFO队列来实现
 *           1.队列里面保存线程的信息，头结点是获取锁的线程
 *           2.其他线程获取锁先通过同步状态status来判断是否可以获取锁（如ReentrantLock的status如果不是当前线程最多是1
 *                  如果是1，则不能获取锁除非是0），如果能获取则获取，否则构造队列节点放入尾部，然后将当前线程挂起
 *           3.释放锁时，释放同步状态status（如ReentrantLock将状态变为0），同时唤醒后继节点
 *           4.在写自定义同步器的时候只需重写tryAcquire，tryRelease，tryAcquireShared, tryReleaseShared几个方法，来决定同步状态的释放和获取即可
 *           5.有共享模式和独占模式：ReentrantLock独占模式，一次只有一个获取锁的线程接口，CountDownLatch共享模式，一次有多个获取锁的线程节点
 *         c.aqs非公平锁和公平锁的实现方式和区别
 *           1.非公平锁，lock的时候，会无视正在等待锁资源的队列里面是否有成员，而直接尝试一次获取，若不成功，则还是会进入AQS的CLH等待队列，然后阻塞，顺序等待唤醒，获取。
 *           2.公平锁，lock的时候，则不能无视正在等待锁资源的队列里面的成员。
 *    3.AQS源码流程：
 *      a.讲的好：https://zhuanlan.zhihu.com/p/65349219
 *      b.condtion原理
 *      1）将当前线程封装成node且等待状态为CONDITION。
 *      2）释放当前线程持有的锁，让下一个线程能获取锁。
 *      3）加入到条件队列后，则阻塞当前线程，等待被唤醒。
 *      4）如果是因signal被唤醒，则节点会从条件队列转移到等待队列。
 *      5）若是因signal被唤醒，就自旋获取锁；否则处理中断异常
 *  九.CountDownLatch，Semaphore等线程同步类
 *          CountDownLatch控制同时等待多少个线程执行结束后再进行，Semaphore可控制有多少个线程同时执行
 *
 *  八.Concurrent同步包各种同步数据结果
 *      参考：https://blog.csdn.net/defonds/article/details/44021605#t8
 *
 *  十.读写锁实现原理
    https://www.toutiao.com/i6714450210241643019/?tt_from=weixin&utm_campaign=client_share&wxshare_count=1&timestamp
   =1621379437&app=news_article&utm_source=weixin&utm_medium=toutiao_ios&use_new_style=1&req_id=202105190710370102120
  7020508213DB6&share_token=CB9AE682-983C-4000-84C1-0A6EDF768EFC&group_id=6714450210241643019&wid=1621386976742
 *   a.将status变量分为高16位读，低16位写，读写互斥，读共享，写互斥
 *   b.将status左移动获取写状态，右移动获取读状态，其他操作和ReentrantLock是一样的
 *   c.在非公平模式下，读操作，如果发现队列头部是有写线程，则会优先让写线程先获取，避免出现写线程饥饿（如果读获取到了，后面新的读锁可以一直获取到，写线程就很少有机会）
 *
 *  16.volatile
 *      参考：https://www.cnblogs.com/chengxiao/p/6528109.html
 *     a.轻量级锁
 *     b.能保证共享变量对所有线程的可见性，当写一个volatile变量时，JMM会把该线程对应的本地内存中的变量强制刷新到主内存中去
 *     c.禁止指令重排序优化
 *     d.volatile对于单个的共享变量的读/写具有原子性，但是像num++这种复合操作，volatile无法保证其原子性,可以用原子锁

 * @author liucan
 * @version 19-1-20
 */
//singleton单例模式,.prototype原型模式,request,session,global session
@Scope
@Component
public class Thread1 {

    private final ThreadLocal<String> tl = new ThreadLocal<>();
    private final Object lock = new Object();
    private final Object lock1 = new Object();
    private boolean waitSignal = true;

    public void example() {
        new Exchanger1().test();
        new Executor1().test();
        new Queue1().test();
        new CyclicBarrier1().test();
        new Semaphore1().test();
        new Future1().test();
        threadTest();
    }

    private void threadTest() {
        Thread thread = new Thread(() -> System.out.println(1));
        thread.setDaemon(true); //必须在启动前调用
        thread.setName("线程");
        thread.start();

        Print print = new Print();
        new Thread(print, "C").start();
        new Thread(print, "A").start();
        new Thread(print, "B").start();

//        new Thread(() -> print.method1()).start();
//        new Thread(() -> print.method5()).start();
//        new Thread(() -> print.method2()).start();

//        new Thread(() -> print.method3()).start();
//        new Thread(() -> print.method5()).start();
//        new Thread(() -> print.method4()).start();

        tl.set("1");
        tl.get();
    }

    private void doWait() {
        synchronized (lock1) {
            //此处用while不用if是因为线程会出现假唤醒（spurious wakeups：由于莫名其妙的原因，线程会在没有notify的情况下唤醒）
            while (waitSignal) {
                try {
                    //wait会释放当前对象上的监控器锁
                    lock1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doNotify() {
        synchronized (lock1) {
            try {
                waitSignal = false;
                lock1.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设计一个程序，启动三个线程A,B,C,各个线程只打印特定的字母，各打印5次，例如A线程只打印‘A’。要求在控制台依次显示“ABCABC…”
     */
    private class Print implements Runnable {

        private String currentPrint = "A";
        private int curCount = 0;

        //给线程执行的
        @Override
        public void run() {
            synchronized (lock) {
                while (curCount < 5) {
                    String threadName = Thread.currentThread().getName();
                    if (threadName.equals(currentPrint)) {
                        System.out.println(currentPrint);
                        if (currentPrint.equals("A")) {
                            currentPrint = "B";
                        } else if (currentPrint.equals("B")) {
                            currentPrint = "C";
                            curCount++;
                        } else {
                            currentPrint = "A";
                        }
                        //notify()方法不释放锁
                        lock.notifyAll();
                    } else {
                        try {
                            //调用wait会释放对象上的监视器锁Monitor锁
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private synchronized void method1() {
            try {
                Thread.sleep(3000);
                System.out.println("method1" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private synchronized void method2() {
            try {
                Thread.sleep(1000);
                System.out.println("method2" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void method3() {
            try {
                synchronized (this) {
                    Thread.sleep(3000);
                    System.out.println("method3" + Thread.currentThread().getName());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void method4() {
            try {
                synchronized (this) {
                    Thread.sleep(1000);
                    System.out.println("method4" + Thread.currentThread().getName());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void method5() {
            System.out.println("method5" + Thread.currentThread().getName());
        }
    }
}
