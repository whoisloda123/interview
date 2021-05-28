package com.liucan.kuroky.multthread;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* *
 *
 * 一线程中断
 *   a.调用Thread.interrupt,只是设置一下中断状态，然后其他线程在执行的判断一下线程状态，然后抛出中断异常
 *   b.对应sleep,join等方法会直接抛出中断异常，然后清除中断状态
 * 二.java内存模型：
 *      https://zhuanlan.zhihu.com/p/29881777
 *      1.主内存与工作内存：线程对变量的所有操作（读取、赋值）都必须在工作内存中进行，而不能直接读写主内存中的变量
 *      2.主内存与工作内存之间的具体交互协议，即一个变量如何从主内存拷贝到工作内存、如何从工作内存同步到主内存之间的实现细节
 *          以下八种操作来完成
 *              lock（锁定）,unlock（解锁）,read（读取）,write（写入）,load（载入）,assign（赋值)等
 *      2.为了优化性能，会对指令进行重排序（编译器，cpu都会重拍序），单线程下没有问题，多线程就有可能有问题了
 *      3.happens-before：
 *          a.happens-before的概念来指定两个操作之间的执行顺序,两个操作可以在一个线程之内，也可以是在不同线程之间
 *          b.可以通过happens-before关系向程序员提供跨线程的内存可见性保证
 *          c.如果一个操作happens-before另一个操作，那么第一个操作的执行结果将对第二个操作可见，
 *              而且第一个操作的执行顺序排在第二个操作之前(程序员视角)，
 *              有可能会指令重排序（JVM视角）
 *          d.具体规则:有6种
 *      4.voatile内存屏障禁止重排序，内存屏障能够保证屏障之前的操作能够优先与屏障之后的操作
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
 * 五.synchronized:https://cloud.tencent.com/developer/article/1465413
 *  1.synchronized锁定是一个对象，其他试图访问该对象synchronized方法或代码块会被锁住,而每一个对象都可以做为一个锁（Monitor锁）
 *  2.在普通方法前面，锁的是当前实例对象（其他的synchronized标志的方法也会被锁住,非synchronized的不会被锁住）
 *  3.在静态方法前面，锁的是整个类
 *  4.在方法块里面synchronized(object),锁的是括号里面的对象
 *  5.实现机制
 *      a.jvm存放对象信息里面的对象头里面有 Mark Word区域（包含了synchronized状态，分代年龄，对象hashcode等）
 *      b.synchronized状态包括：无状态，偏向锁，轻量级锁，重量级锁
 *      a.每个对象有个监视器锁（Monitor锁）
 *      c.notify/notifyAll和wait方法都依赖Monitor锁.synchronized方法，和方法块是基本Monitor锁实现，执行时候进入获取锁，离开释放锁
 *      f.wait()方法立即释放对象监视器，notify()/notifyAll()方法则会等待线程剩余代码执行完毕才会放弃对象监视器
 *  7.锁膨胀
 *      a.偏向锁:减少统一线程获取锁的代价,线程再次请求锁时，获取锁的过程检查Mark Word区域锁标记位为偏向锁以及是否是当前线程，如果是则直接获取线程
 *      b.轻量级锁:指当锁是偏向锁的时候，被另一个线程所访问，偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，提高性能。
 *      c.重量级锁是指当锁为轻量级锁的时候，另一个线程虽然是自旋，但自旋不会一直持续下去，当自旋一定次数的时候，还没有获取到锁，就会进入阻塞，
 *              该锁膨胀为重量级锁。重量级锁会让其他申请的线程进入阻塞，性能降低
 *      d.自旋锁（无锁）:线程不会阻塞，不会释放cpu时间片，而一直循环等待，采用原子锁cas（compare and swap）方式
 *  8.锁消除:除锁是虚拟机另外一种锁的优化,编译时，对运行上下文进行扫描，去除不可能存在竞争的锁
 *  9.锁粗化：锁粗化指的是有很多操作都是对同一个对象进行加锁，就会把锁的同步范围扩展到整个操作序列之外
 * 六.线程同步
 *  2.sleep,yield,wait区别
 *      a.sleep后，不会释放当前锁，会释放cpu时间片，暂停线程，时间到线程处于可以调用状态
 *      b.yield后，不会释放当前锁，会释放cpu时间片，线程处于可以调度状态（ps：可能出现yield后，马上又被调用，完全取决于线程调度器）
 *      c.wait后，会释放当前锁，会释放cpu时间片，暂停当前线程，直到被notify/notifyAll通知
 *  4.ThreadLocal：https://zhuanlan.zhihu.com/p/102571059
 *      1.ThreadLocal里面是保存的是map（当前线程对应的弱引用key，对应value）
 *      2.内存泄漏的原因：弱引用key被jvm回收之后，key为空了，但是value还在，但是没有能够获取到value的地方了，导致内存泄露
 *      3.为什么使用弱引用而不是强引用：如果没有手动删除，ThreadLocal不会被回收，导致Entry内存泄漏
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
 * 十一.cas的aba问题
 * https://zhuanlan.zhihu.com/p/87908087
 * a.线程1，2同时拿到一样A值，1线程执行快cas将改成了B，又换成了A，2线程执行慢在cas的是否发现值还是a，不知道A值其实已经被变过了
 * b.像银行转账一样会有问题，可以用AtomicStampedReference，获取用mysql乐观锁加版本号
 * 十二.interview
 * https://www.toutiao.com/i6966563726115799585/?tt_from=weixin&utm_campaign=client_share&wxshare_count=1&timestamp=1622073038&app=news_article&utm_source=weixin&utm_medium=toutiao_ios&use_new_style=1&req_id=2021052707503701021207008633286ACF&share_token=1B72304C-E596-4EB3-B339-CF429CDD9BBE&group_id=6966563726115799585&wid=1622125496351
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
