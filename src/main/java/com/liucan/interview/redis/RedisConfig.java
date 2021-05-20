package com.liucan.interview.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * 一.redis集群哨兵
 *  参考：http://youzhixueyuan.com/redis-high-availability.html
 *  1.哨兵
 *      a.集群监控：负责监控Redis master(读写)和slave(读)进程是否正常工作
 *      b.消息通知：如果某个Redis实例有故障，那么哨兵负责发送消息作为报警通知给管理员
 *      c.故障转移：如果master node挂掉了，会自动转移到slave node上
 *      d.配置中心：如果故障转移发生了，通知client客户端新的master地址
 *  2.集群
 *      a.即使使用哨兵，redis每个实例也是全量存储，每个redis存储的内容都是完整的数据，浪费内存
 *      b.解决单机Redis容量有限的问题，将数据按一定的规则分配到多台机器
 *      c.每个key会对应一个slot，总共16384个slot均分到不同的节点上面,至少需要3主3从
 *  3.投票选举
 *      a.所有master参与，每个master都和其他master连接上了的，如果半数以上的master认为某个节点挂掉，就真的挂掉了
 *      b.根据各个slave最后一次同步master信息的时间，越新表示slave的数据越新，竞选的优先级越高，就更有可能选中.
 *  4.主从复制
 *  http://blog.itpub.net/31545684/viewspace-2213629/
 *      a.从数据库启动时，向主数据库发送sync命令，主数据库接收到sync后开始在快照rdb，在保存快照期间受到的命名缓存起来，
 *          快照完成时，主数据库会将快照和缓存的命令一块发送给从
 *      b.主每收到1个写命令就同步发送给从
 *
 * 二.单线程为何还快
 *  1.基于内存操作,内存操作非常快，所以说没有必要多线程
 *  2.不存在多进程或者多线程导致的切换而消耗CPU，而多线程会处理锁
 *  3.使用epoll多路 I/O 复用模型来处理多并发客户端
 *
 * 三.持久化
 *  1.RDB快照
 *      a.默认开启，按照配置指定时间将内存中的所有数据快照到磁盘中，创建一个dump.rdb文件，redis启动时再恢复到内存中。
 *      b.单独创建fork子进程，将父进程的数据复制到子进程的内存中，然后由子进程写入到临时文件中，过程结束了，再用这个临时文件替换上次的快照文件
 *  2.AOF
 *      a.每隔一段时间（默认每一秒）或者有写操作时将执行写命令append写入文件，启动时会根据日志从头到尾全部执行一遍以完成数据的恢复工作。包括flushDB也会执行
 *      b.随着redis一直运行，aof文件会变的很大，可通过BGREWRITEAOF命令或者设置参数redis会启动aof文件缩减命令
 *          1.里面的有些多条指令可以合并
 *          2.无效的命令可以删除掉，如del了key的命令可以删除掉
 *  3.当两种方式同时开启时，数据恢复redis会优先选择AOF恢复。一般情况下只要使用默认开启的RDB即可，因为相对于AOF，RDB便于进行数据库备份，
 *      并且恢复数据集的速度也要快很多
 *
 * 四.分布式锁
 *   setnx,setex2个命令来实现
 *
 * 五.缓存问题
 *  1.缓存雪崩：缓存同一时间大面积的失效，这个时候又来了一波请求，结果请求都怼到数据库上，从而导致数据库连接异常
 *      解决方法：给缓存的失效时间，加上一个随机值，避免集体失效。
 *  2.缓存穿透：黑客故意去请求缓存中不存在的数据，导致所有的请求都怼到数据库上，从而数据库连接异常
 *      解决方法：为空也保存到内存里面
 *  3.缓存击穿：一个key失效，刚好很多请求来了，一般可以用，只对几个请求区处理数据库，然后其他的等待，或者直接返回空,可以用加锁的方式实现
 *  3.缓存预热：目的就是在系统上线前，将数据加载到缓存中
 *  等
 *
 * 六.事务
 *   1.集群不支持事务
 *      a.事务里面操作可以在不同节点,对于多个key的操作,保证不了不会被其他命令插进来,无法保证其原子性,但集群里边的每个节点支持事务，
 *      b.事务不支持,因为redis不是关系型数据库，是key-value不关心业务，没有必要
 *   2.集群不支持pipeline管道
 *      a.批量操作位于不同的slot的时候会失败,因为slot在不同的node,但也支持单个节点
 *      b.单个节点操作：通过key计算出slot，然后通过slot找到对应的节点，然后在节点上面操作
 *   3.集群不支持对所有键进行scan操作
 *   4.事务是支持原子的，通过watch命令，当在执行事务的时候，被其他修改，就执行失败
 *   5.事务不支持回滚操作，事务过程中有一条执行失败了，剩下的会继续执行
 *   6.过程是：先watch，然后multi,执行命令，然后exec
 *
 * 七.内存回收策略
 *   1.当使用内存达到一定大小的时候,会实行回收策略
 *   2.策略方式:
 *      a.voltile-lru：从已设置过期时间的数据集中挑选最近最少使用的数据淘汰
 *      b.volatile-ttl：从已设置过期时间的数据集中挑选将要过期的数据淘汰
 *      c.volatile-random：从已设置过期时间的数据集中任意选择数据淘汰
 *      d.allkeys-lru：从数据集中挑选最近最少使用的数据淘汰
 *      e.allkeys-random：从数据集中任意选择数据淘汰
 *      f.no-enviction（驱逐）：禁止驱逐数据
 *
 * 八.发布订阅
 *   1.消息订阅者，即subscribe客户端，需要独占链接，即进行subscribe期间，redis-client无法穿插其他操作，
 *      此时client以阻塞的方式等待“publish端”的消息；甚至需要在额外的线程中使用
 *   2.消息发布者，即publish客户端，无需独占链接
 *   3.Pub/Sub功能缺点是消息不是持久化的，发送就没有了
 *
 * 九.redis 延迟队列
 *   1.就是将消息放入zset里面，score为过期时间，然后有专门的线程去取最近的时间，拿出来消费，然后在删除掉
 *   2.应用场景：下单成功，30分钟之后不支付自动取消，等
 * 十。Redis的rehash为什么要渐进rehash，渐进rehash又是怎么实现的?
 * 因为redis是单线程，当K很多时，如果一次性将键值对全部rehash，庞大的计算量会影响服务器性能，
 * 甚至可能会导致服务器在一段时间内停止服务。不可能一步完成整个rehash操作，所以redis是分多次、渐进式的rehash。渐进性哈希分为两种：
 * 1）操作redis时，额外做一步rehash
 *
 * 对redis做读取、插入、删除等操作时，会把位于table[dict->rehashidx]位置的链表移动到新的dictht中，然后把rehashidx做加一操作，移动到后面一个槽位。
   十一.reids底层数据结构和常用类型用的数据结构
     https://cloud.tencent.com/developer/article/1690533
 *
 * 2）后台定时任务调用rehash
 *
 * 后台定时任务rehash调用链，同时可以通过server.hz控制rehash调用频率
 * 二.spring-redis
 *  参考：https://www.cnblogs.com/EasonJim/p/7803067.html（spring-redis）
 *      http://blog.51cto.com/aiilive/1627455（spring-redis）
 *
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "redis")
@PropertySource(value = "classpath:properties/redis.properties")
public class RedisConfig {

}
