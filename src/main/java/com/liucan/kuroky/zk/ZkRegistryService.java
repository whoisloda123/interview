package com.liucan.kuroky.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * zookeeper:分布式系统中的协调系统，提供配置管理、集群管理(服务注册，服务加入和退出)、分布式锁和分布式队列
 *  本身就是集群模式
 * 参考：https://www.cnblogs.com/felixzh/p/5869212.html
 * 一.概念
 *  1.文件系统：
 *      核心是类似于linux文件系统，节点类型：持久化目录节点 、持久化顺序目录节点 、临时目录节点、临时顺序目录节点
 *  2.通知机制：
 *      只关心监听他的目录节点，当目录节点的发生改变（数据改变、被删除、子目录节点增加删除）会通知
 *  3.工作原理
 *      a.zk核心是原子广播，采用zab协议，该协议有2种模式：恢复模式（选主）和广播模式（通过状态）
 *  4. 角色
 *      a.leader:负责投票协议的发起和决议，更新系统状态
 *      b.follower:参与投票
 *      c.observer:不参与投票，将请求转发给leader，目的是扩展系统，提供读取速度
 *
 * 二.作用
 *  1.配置管理：
 *      服务节点的配置（mysql的ip，port，redis的地址等）放zookeeper，且发生改变时会通知所有服务
 *  2.dns服务
 *      创建/dns/app1/www.service.company.com 节点的内容为host1:port1,host2:port2
 *      Dubbo 就是使用 Zookeeper 作为域名服务器的
 *  3.集群管理(是否有机器退出和加入、选举master)
 *      a.是否有机器退出和加入:所有机器在父目录下建立临时顺序子节点，监听父目录下子节点变化，所有临时节点加入和删除都会通知到其他节点
 *      b.maser选举
 *          1.多个同时发起对同一临时节点进行创建，只有一个可以成功，该成功者即为 Master，其它为 Slave
 *          2.Slave 都向该临时节点的父节点注册一个子节点列表变更的 watcher 监听
 *          3.一旦该 Master 宕机，临时节点即会消失。引发子节点列表变更事件，触发各个 Slave 的 watcher 回调的执行。重新发起向 zk 注册临时节
 *              点的请求，注册成功的即为新的 Master
 *  5.分布式锁
 *      a.独占锁：创建 /distribute_lock节点成功的则获得锁，其他则监控该节点，释放锁则删除该节点，其他节点收到删除通知后进行创建 /distribute_lock节点
 *      b.公平锁： /distribute_lock已经存在，创建该节点临时顺序子节点，和选举master一样的，最小节点获得锁，同时子节点监听比自己小一点的子节点
 *  6.分布式队列
 *      a.同步队列，当一个队列的成员都聚齐时，这个队列才可用，否则一直等待所有成员到达（在约定目录下创建临时目录节点，监听子节节点数目是否是我们要求的数目）
 *      b.队列按照 FIFO 方式进行入队和出队操作(和分布式锁一样的，最小的先出队列)

 * 三.zk和eureka区别
 *     https://mp.weixin.qq.com/s/YDHk8uwN8VAkt8NXEs4lRg
 *   1.zk：cp是强一致性，当leader挂了之后，进行选举一般200ms-60000ms，期间整个集群不可用
 *   2.eureka：ap是最终一致性，选举期间还可以使用，只是不是最新的
 *      a.点对点，没有主从，节点之间通过心态机制感知对方
 *      b.如果eureka service挂掉之后，eureka client会自动切换到新的eureka service节点上面
 *      c.当宕机的服务器重新恢复后，Eureka 会再次将其纳入到服务器集群管理之中,尝试从邻近节点获取所有注册列表信息
 *      d.Eureka Server 节点在短时间内丢失过多的心跳时，那么这个节点就会进入自我保护模式(不再移除因为长时间没有收到心跳而过期的服务，
 *        仍然能够接受新服务注册和查询请求，但是不会被同步到其它节点上)
 */
@Slf4j
@Service
public class ZkRegistryService implements Watcher {
    private static final int SESSION_TIMEOUT = 30000;
    private static final String REGISTRY_PATH = "/registry";
    private CountDownLatch registryLock = new CountDownLatch(1);
    private ZooKeeper zk;
    @Value("${registry.servers}")
    private String zkConnect;

    @PostConstruct
    public void init() {
        try {
            log.info("[zk服务注册]开始连接zookeeper");
            //禁用sasl认证，否则会报错,不禁用不影响运行
            System.setProperty("zookeeper.sasl.client", "false");
            //异步创建的
            zk = new ZooKeeper(zkConnect, SESSION_TIMEOUT, this);
            ;
            log.info("[zk服务注册]等待连接zookeeper.....");
            registryLock.await(100, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[zk服务注册]创建zk失败", e);
        }
    }

    @PreDestroy
    public void close() {
        if (zk != null) {
            try {
                zk.close();
                log.info("[zk服务注册]关闭zookeeper成功");
            } catch (Exception e) {
                log.error("[zk服务注册]关闭zookeeper异常", e);
            }
        }
    }

    public void register(String serviceName) {
        String data = "";
        try {
            //创建根节点：持久节点
            if (zk.exists(REGISTRY_PATH, false) == null) {
                zk.create(REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("[zk服务注册]创建rootNode:{}", REGISTRY_PATH);
            }
            //创建服务节点：持久节点
            String servicePath = REGISTRY_PATH + "/" + serviceName;
            if (zk.exists(servicePath, false) == null) {
                zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("[zk服务注册]创建serviceNode:{}", servicePath);
            }
            //创建地址节点：临时顺序节点
            data = InetAddress.getLocalHost().getHostAddress();
            String addressPath = servicePath + "/address-";
            String addressNode = zk.create(addressPath, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("[zk服务注册]注册服务成功addressNode:{}", addressNode);
        } catch (Exception e) {
            log.error("[zk服务注册]注册异常,serviceName:{},data:{}", serviceName, data, e);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected) {
            registryLock.countDown();
            log.info("[zk服务注册]连接zookeeper成功....");
            //注册服务
            log.info("[zk服务注册]开始注册服务....");
            register("java-learn");
        }
    }
}
