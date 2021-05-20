package com.liucan.interview.multthread;

import java.util.concurrent.*;

/**
 * @author liucan
 * @version 19-3-2
 */
public class Queue1 {

    /**
     * 同步队列
     * http://ifeve.com/java-synchronousqueue/
     * 1.队列的大小为0
     * 2.在put的时候，要等待另外的线程take，反之一样
     * 3.相对于生产者和消费者相互等待，直到握手一起离开
     * 4.Executors.newCachedThreadPool()里面用的就是SynchronousQueue
     */
    private void synchronousQueue() {
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();
    }

    private void arrayBlockingQueue() {
        //ArrayBlockingQueue
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1024);
        new Thread(new Consumer(blockingQueue)).start();
        new Thread(new Producer(blockingQueue)).start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void linkedBlockingQueue() {
        //LinkedBlockingQueue
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>(1024);
        try {
            linkedBlockingQueue.put("1");
            String take = linkedBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void delayQueue() {
        //在每个元素的getDelay()方法返回的值的时间段之后才释放掉该元素,
        //如果返回的是 0 或者负值，延迟将被认为过期，该元素将会在 DelayQueue 的下一次 take  被调用的时候被释放掉。
        Delayed delayed = new Delayed() {
            @Override
            public long getDelay(TimeUnit unit) {
                return 0;
            }

            @Override
            public int compareTo(Delayed o) {
                return 0;
            }
        };

        DelayQueue<Delayed> delayQueue = new DelayQueue();
        delayQueue.put(delayed);
        try {
            Delayed take = delayQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void priorityBlockingQueue() {
        //PriorityBlockingQueue带排序的队列和PriorityQueue差不多
        //thenComparing用在当第一个比较器相等的情况下的比较
        //PriorityBlockingQueue不保证在第一个比较器相等的情况下，第二个比较器相等

    }

    private void priorityQueue() {

    }

    private void linkedBlockingDeque() {
        LinkedBlockingDeque<String> linkedBlockingDeque = new LinkedBlockingDeque<>(10);
    }

    public void test() {
        arrayBlockingQueue();
        linkedBlockingQueue();
        delayQueue();
        priorityBlockingQueue();
        priorityQueue();
        linkedBlockingDeque();
    }

    private class Producer implements Runnable {

        private final BlockingQueue<String> blockingQueue;

        public Producer(BlockingQueue<String> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            try {
                blockingQueue.put("1");
                Thread.sleep(1000);
                blockingQueue.put("2");
                Thread.sleep(1000);
                blockingQueue.put("3");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Consumer implements Runnable {
        private final BlockingQueue<String> blockingQueue;

        public Consumer(BlockingQueue<String> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            try {
                System.out.println(blockingQueue.take());
                System.out.println(blockingQueue.take());
                System.out.println(blockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
