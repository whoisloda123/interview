package com.liucan.interview.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author liucan
 * @date 2018/7/28
 * @brief zookeeper分布式写锁
 *        1.参考资料：https://blog.csdn.net/peace1213/article/details/52571445
 *        2.创建wirte_lock持久节点，顺序临时子节点
 *        3.lock时候getchild（不需要监控，会有羊群效应）判断 临时最小节点是否是最小的
 *        4.如果是则，获得锁，否则监听比自己小一个节点的exist，事件发生获得锁
 *        5.unlock直接删除节点
 *        6.里面在perZnode在unlock的是有点问题，通知不到
 *        7.可以使用Apache Curator封装Zookeeper操作的库
 */
@Slf4j
public class ZkDistributedWriteLock implements Watcher, Lock {
    private ZooKeeper zk;
    private String rootZnode = "/write-locks";
    private String perZnode; //前一个node
    private String curZnode; //当前node
    private String lockName;//竞争资源的标志
    private CountDownLatch waitPerSignal; //等待perNode释放
    private CountDownLatch connectedSignal = new CountDownLatch(1); //建立连接用
    private static final int sessionTimeout = 30000;

    /**
     * 创建分布式锁,使用前请确认config配置的zookeeper服务可用
     *
     * @param zkServers 192.168.32.128:2181
     * @param lockName  业务竞争资源标志,lockName中不能包含单词_lock_
     */
    public ZkDistributedWriteLock(String zkServers, String lockName) {
        this.lockName = lockName;
        try {
            log.info("[分布式锁]开始创建zookeeper分布式写锁对象");
            //禁用sasl认证，否则会抛异常,不禁用不影响运行
            System.setProperty("zookeeper.sasl.client", "false");
            zk = new ZooKeeper(zkServers, sessionTimeout, this);
            connectedSignal.await(); //等待连接建立
            Stat stat = zk.exists(rootZnode, false);
            if (stat == null) {
                zk.create(rootZnode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("[分布式锁]创建rootNode成功：rootZnode:{}", rootZnode);
            }
        } catch (Exception e) {
            throw new LockException(e);
        }
    }

    /**
     * zookeeper节点的监视器
     */
    @Override
    public void process(WatchedEvent event) {
        //建立连接
        if (event.getState() == Event.KeeperState.SyncConnected) {
            connectedSignal.countDown();
            log.info("[分布式锁]创建zookeeper分布式写锁对象成功");
            return;
        }

        //其他线程放弃锁的标志,此处只会收到del消息
        if (waitPerSignal != null) {
            waitPerSignal.countDown();
            log.info("[分布式锁]监听到其他节点unlock锁：Znode：{}", event.getPath());
        }
    }

    @Override
    public void lock() {
        try {
            if (tryLock()) {
                log.info("[分布式锁]lock获得锁成功：curZnode：{}", curZnode);
            } else {
                waitForLock(perZnode, sessionTimeout);
            }
        } catch (Exception e) {
            throw new LockException(e);
        }
    }

    @Override
    public boolean tryLock() {
        try {
            String splitStr = "_lock_";
            if (lockName.contains(splitStr)) {
                throw new LockException("lockName can not contains " + splitStr);
            }
            //创建临时顺序子节点
            curZnode = zk.create(rootZnode + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("[分布式锁]创建临时顺序子节点：curZnode：{}", curZnode);
            //取出所有子节点,不需要监控，会有羊群效应
            List<String> subZnodes = zk.getChildren(rootZnode, false);
            //取出所有lockName的锁
            List<String> lockObjNodes = new ArrayList<>();
            for (String subZnode : subZnodes) {
                String _subZnode = subZnode.split(splitStr)[0];
                if (_subZnode.equals(lockName)) {
                    lockObjNodes.add(subZnode);
                }
            }
            Collections.sort(lockObjNodes);
            //如果是最小的节点,则表示取得锁
            if (curZnode.equals(rootZnode + "/" + lockObjNodes.get(0))) {
                log.info("[分布式锁]tryLock获得锁成功：curZnode：{}", curZnode);
                return true;
            }
            //如果不是最小的节点，找到比自己小1的节点
            String subCurZnode = curZnode.substring(curZnode.lastIndexOf("/") + 1);
            perZnode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subCurZnode) - 1);
        } catch (Exception e) {
            throw new LockException(e);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        try {
            if (tryLock()) {
                return true;
            }
            return waitForLock(perZnode, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void unlock() {
        try {
            String delZnode = curZnode;
            zk.delete(curZnode, -1);
            curZnode = null;
            log.info("[分布式锁]unlock锁：curZnode：{}", delZnode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lockInterruptibly() {
        lock();
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    public void close() {
        try {
            zk.close();
        } catch (Exception e) {
            log.info("[分布式锁]关闭zk连接：rootZnode：{}", rootZnode);
        }
    }

    /**
     * 等待上一个node
     */
    private boolean waitForLock(String perZnode, long waitTime) throws InterruptedException, KeeperException {
        //注册监听
        Stat stat = zk.exists(rootZnode + "/" + perZnode, true);
        if (stat != null) {
            log.info("[分布式锁]threadId:{}, 等待perZnode:{}", Thread.currentThread().getId(), perZnode);
            waitPerSignal = new CountDownLatch(1);
            //等待，这里应该一直等待其他线程释放锁
            waitPerSignal.await(waitTime, TimeUnit.MILLISECONDS);
            waitPerSignal = null;
        }
        return true;
    }

    public class LockException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public LockException(String e) {
            super(e);
        }

        public LockException(Exception e) {
            super(e);
        }
    }
}
