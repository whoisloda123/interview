package com.liucan.kuroky.kafka.common;

import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liucan
 *
 * 一.概念:
 *  a.producer生产者,broker:kafka集群,consumer消费者,topic消息类型,cunsumer-group消费组
 *  b.partition:分片,一个topic包含多个partition
 *  c.leader:多个partiton里面的一个角色,consumer和producer只和leader交互
 *  d.follower:
 *      1.多个partiton里面的多个角色,只负责同步leader数据,不会和customer和producter交互，异步拉取数据
 *      2.当leader挂掉之后，follower通过zk可以感知到，然后进行选举leader,在选举新leader时，从isr列表中的follower进行选举(轮流坐庄)
 *      3.isr列表里面是和leader同步的follower（副本落后leader在设置的时间内默认10秒）
 *      4.Unclean Leader Election:控制是否isr为空的情况，还能否进行选举
 *  e.zookeeper:存储集群的信息
 *
 * 二.特点：
 *  a.⾼吞吐、低延迟: kakfa 最⼤的特点点点点点点点点点就是收发消息⾮常快，kafka 每秒可以处理⼏⼗万条消息，它的最低延迟只有⼏毫秒
 *  b.⾼伸缩性: 每个主题(topic) 包含多个分区(partition)，主题中的分区可以分布在不同的主机(broker)中
 *  c.持久性、可靠性: Kafka 能够允许数据的持久化存储，消息被持久化到磁盘，并⽀持数据备份防⽌数据丢失
 *  d.容错性: 允许集群中的节点失败，某个节点宕机，Kafka 集群能够正常⼯作
 *  e.⾼并发: ⽀持数千个客户端同时读写
 *
 * 三.日志组成
 *  a.partition进⼀步细分为了若⼲的segment，每个segment逻辑⽂件的最⼤⼤⼩相等
 *  b.segment逻辑文件又包括了2个文件，.index文件和.log文件，log文件保存消息，.index文件保存了消息的offset和length
 *  c.⼀个 Segment 中消息的存放是顺序存放的
 *  d.Segment ⽂件越来越多，为了便于管理，将同⼀ Topic 的 Segment ⽂件都存放到⼀个或多个⽬录中，这些⽬录就是 Partition
 *
 * 四.HW(high watermark):高水位
 *  a.Consumer可以消费到的最⾼partition偏移量,因为leader写入到partiton里面，但是因为follower未同步，还未cimmit，不能消费
 *  b.作用：
 *      定义消息可⻅性，即⽤来标识分区下的哪些消息是可以被消费者消费的
 *      帮助 Kafka 完成副本同步,保证leader和follower之间的数据⼀致性
 *
 * 五.发送的可靠性机制
 *  a.acks=0:效率最⾼，吞吐量⾼，但可靠性最低。其可能会存在消息丢失的情况
 *  b.acks=1:只要集群的 Leader 节点收到消息，⽣产者就会收到⼀个来⾃服务器的成功响应
 *      该⽅式不能使producer确认其发送的消息是成功的，但可以确认消息发送是失败的
 *  c.acks=all:当所有参与复制的节点全部收到消息时,才会返回
 *      可靠性最⾼，很少出现消息丢失的情况。但可能会出现部分follower重复接收的情况，因为producer可能会超时重发
 *
 * 二.producer生产消息
 *  a.写入方式:push 模式将消息发布到 broker,每条消息append到partition中,顺序写磁盘
 *  b.消息路由:发送消息时,根据分区算法找到一个paration,涉及到topic,paration,key,msg
 *      1.指定paration则直接使用
 *      2.未指定 patition 但指定 key，通过对 key 的 value 进行hash 选出一个 patition
 *      3.patition 和 key 都未指定，使用轮询选出一个 patition
 * c.写入流程:
 *      1.producer 从controller(zookeeper中)找到该 partition 的 leader
 *      2.消息发送给leader,leader写入本地log
 *      3.followers从leader中pull消息,发送ack
 *      4.leader收到所有followers的ack后,提交commmin,然后返回producer,相当于要所有的followers都有消息才会commit
 * d.消息发送类型
 *      1.at-least-once:producer收到来自borker的确认消息,则任务发送成功,但可能会出现broker处理完消息了,但发送确认消息异常了
 *          producer会重试,导致broker接收2次
 *      2.at-most-once:不管producer发送消息返回超时或者失败,都不会重试
 *      3.exactly-once:即使producer重试发送消息，消息也会保证最多一次地传递
 *          实现方式:
 *          a.幂等:
 *              概念:单个paratition里面,不管producer发送多少次,都只会给写入一次
 *              实现方式:brokder端维护一个序号,每条消息都有序号,如果发送的序号比当前最大的序号大1则表示是新消息保存,
 *                      若大1以上,则认为乱序,小于则认为是老消息,抛弃掉
 *              解决问题:
 *                  a.Broker保存消息后，发送ACK前宕机，Producer认为消息未发送成功并重试，造成数据重复
 *                  b.前一条消息发送失败，后一条消息发送成功，前一条消息重试后成功，造成数据乱序
 *
 *          b.事务:多个paratition的原子操作,发送批量消息到多个paratition要么全部成功要么全部失败
 *              实现方式:
 *
 * 三.broker保存消息
 *  a..存储方式:物理上把 topic 分成一个或多个 patition（对应 server.properties 中的 num.partitions=3 配置）
 *  ，每个 patition 物理上对应一个文件夹（该文件夹存储该 patition 的所有消息和索引文件）
 *  b.存储策略:无论消息是否被消费，kafka 都会保留所有消息
 *  c.topic创建
 *      1.controller 在 ZooKeeper 的 /brokers/topics 节点上注册 watcher，当 topic 被创建，则 controller
 *          会通过 watch 得到该 topic 的 partition/replica 分配
 *      2.controller从 /brokers/ids 读取当前所有可用的 broker 列表,然后选leader等等
 *  d.topic删除
 *      1.controller 在 zooKeeper 的 /brokers/topics 节点上注册 watcher，当 topic 被删除，
 *          则 controller 会通过 watch 得到该 topic 的 partition/replica 分配。
 *      2.后续操作
 * e.replication
 *  当 partition 对应的 leader 宕机时，需要从 follower 中选举出新 leader。
 *      在选举新leader时，一个基本的原则是，新的 leader 必须拥有旧 leader commit 过的所有消息，从isr列表中
 *      的follower进行选举，isr列表里面是和leader同步的follower（副本落后leader在设置的时间内默认10秒）
 *
 * 四.consumer 消费消息
 *  a.consumer group
 *      1.一个消息只能被 group 内的一个 consumer 所消费，且 consumer 消费消息时不关注 offset，最后一个 offset 由 zookeeper 保存。
 *        后面版本是保存在集群的_consumer_offsets里面的，因为zk并不适合进⾏频繁的写更新
 *          但是多个 group 可以同时消费这个 partition
 *      2.当所有consumer的consumer group相同时，系统变成队列模式
 *      3.当每个consumer的consumer group都不相同时，系统变成发布订阅
 * b.消费方式:consumer 采用 pull 模式从 broker 中读取数据
 *      1.push模式容易造成消费者压力大
 *      2.pull可让消费者自己处理,简化kafka-borker设计,劣势在于当没有数据，会出现空轮询，消耗cpu。
 * c.消息处理方式:
 *      1.At most once最多一次,读完消息马上commit,然后在处理消息,如果处理消息异常,则下次不会在读到上一次消息了
 *      2.At least once(默认方式)最少一次,读完消息,然后处理消息,如果因异常导致没有commit,下次会重新读取到
 *      3.Exactly once刚好一次,比较难
 *
 * 五.如何保证消息的有序性消费
 *  https://blog.csdn.net/bigtree_3721/article/details/80953197
 *  https://www.cnblogs.com/windpoplar/p/10747696.html
 *      1.一个topic，可以指定同一个key，这样消息只会发送到一个partition，但失去了集群优势
 *      2.在消费时，针对每个message不用启用多线程，否者一样会有错乱，针对partition的里面不同的key（不同的key根据分区算法，可能到同一个分区）
 *          的message，放到一个队列里面，顺序消费
 *
 * 六.数据堆积
 *      1.查看消费堆积情况，bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-group
 *      2.出现场景
 *          重复消费：poll的数据业务处理时间不能超过kafka的max.poll.interval.ms，kafka0.10.2.1里面是300s
 *              如果超时了则会失败，这样下次会从新消费，又失败，这样会一直重复消费
 *          消费线程太少：
 *      3.如何解决
 *
 * 七.如何保证消息不丢失不重复消费
 *  https://blog.csdn.net/weixin_38750084/article/details/82939435
 *  https://msd.misuland.com/pd/2884250068896974728
 *  https://www.e-learn.cn/content/qita/934559
 *   a.不丢失
 *      1.消息丢失场景：
 *          a.ack设置为0或者1
 *          b.producer配置的缓冲池满
 *          c.设置 unclean.leader.election.enable = true，支持不清楚的选举，没有在isr列表里面的follower也可以选举
 *          d.没有follower
 *   b.不重复消费
 *      1.改成手动消费，消费完后才提交offset
 *
 * 八.rebalance
 *  a.consumer group:多个consumer组成起来的一个组，共同消费topic所有消息，且一个topic的一个partition(想到于一条信息)只能被一个consumer消费
 *  b.概念：⼀个 Consumer Group 下的所有 Consumer 如何达成⼀致，来分配订阅多个 Topic 的每个分区，达到均衡，效率最大
 *  c.触发条件：
 *      1.组内成员发生变化
 *      2.订阅topic发生变化
 *      3.订阅的topic的分区发生变化
 *  d.消费分区分配算法
 *      1.RangeAssignor:对某一个topic的partition，消费者总数和分区总数整除运算来获得跨度，将分区按照跨度平均分配，以保证分区尽可能均匀地分配给所有的消费者
 *      2.RoundRobinAssignor：消费组内订阅的所有Topic的分区及所有消费者进⾏排序后尽量均衡的分配（RangeAssignor是针对单个Topic的分区进⾏排序分配的）
 *  e.问题：
 *      1.所有消费暂停，stw
 *      2.Rebalance 速度慢：计是所有 Consumer 实例共同参与，全部重新分配所有分区
 *
 * 九.kafka为何速度快（⾼吞吐率实现）
 *  a.分区多个parpation，每个parpation有master和flower
 *  b.顺序读写，消息是不断的aof追加到文件结尾的
 *  c.零拷贝，⽣产者、消费者对于kafka中消息的操作是采⽤零拷贝实现的，利⽤linux操作系统的 "零拷贝（zero-copy）
 *    通过linux系统提供的sendfile，直接把内核缓冲区里的数据拷贝到 socket 缓冲区里，不再拷贝到用户态，在从用户态拷贝到socket缓冲区
 *  d.批量发送和消息压缩
 *  e.读写性能， Kafka利⽤了操作系统本身的Page Cache，就是利⽤操作系统⾃身的内存⽽不是JVM空间内存
 *
 * 十.日志清理策略
 *  https://cloud.tencent.com/developer/article/1165361
 *  a.delete默认
 *      1.自动清除7天之前，或者总大小大于多少之后的数据
 *      2.直接在segment逻辑文件（index,log）后面加.delete后缀，后台定时任务去删除.delete文件
 *  b.compact
 *      1.不会删除数据，只是去重清理
 *      2.对应同一个partition下同一个segment里面，会对相同的key进行替换
 *      3.默认脏数据（重复的key的数据）达到了总数据segment的50%才会执行压缩清理
 *          生成一个新的segment逻辑文件，里面去掉老的重复的数据，老的文件加.delete后缀
 *      4.压缩过程，如果相同的key的value为null，则会删除该数据，可用于删除某个message数据
 *      5.适用于需要长时间保存某些业务数据的场景
 *
 * 十一.消费者同步⼿动提交
 *  a.⾃动提交：enable.auto.commit=true，每隔一段时间自动提交， 可能会出现消息重复消费的情况
 *  b.⼿动提交分类:
 *      1.同步提交：consumer.commitSync()，自动提交上一次poll消息的offset
 *      2.异步提交：consumer.commitAsync(callbacks);增加了消费者的吞吐量
 *      3.同异步提交：异步提交会出现重复消费（消费出现了异常，未commit这次消息），可在异步callback
 *          里面出现异常的时候手动同步提交
 */
@Slf4j
@Service
public class KafkaService {
    private final Producer<String, String> producer;
    private final ConsumerConfig consumerConfig;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final ApplicationContext applicationContext;
    private ConsumerConnector consumerConnector;

    public KafkaService(Producer<String, String> producer,
                        ConsumerConfig consumerConfig,
                        ThreadPoolTaskExecutor threadPoolTaskExecutor,
                        ApplicationContext applicationContext) {
        this.producer = producer;
        this.consumerConfig = consumerConfig;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.applicationContext = applicationContext;
    }

    /**
     * 里面会自己维护消费的offset(保存zk里面)
     * 如果是自己处理的话一般
     * 1.topic通过分区算法找到partition
     * 2.通过partition找到leader partition
     * 3.找到上一次消费的offset,开始消费
     * 4.消费完,commit给broker,保存offset到zk
     */
    @PostConstruct
    public void init() {
        Map<String, List<IKafkaConsumer>> kafkaConsumerMap = kafkaConsumers();
        consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);

        Map<String, Integer> topicCountMap = new HashMap<>();  //描述读取哪个topic，需要几个线程读
        topicCountMap.put("topic-logger", 2);
        topicCountMap.put("topic-order", 2);
        //创建消息处理的流
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumerConnector.createMessageStreams(topicCountMap);

        messageStreams.forEach((topic, listPartition) -> {
            List<IKafkaConsumer> iKafkaConsumers = kafkaConsumerMap.get(topic);
            listPartition.forEach(partition -> {
                //为每个stream(partition)启动一个线程消费消息
                new Thread(() -> {
                    ConsumerIterator<byte[], byte[]> iterator = partition.iterator();
                    //it.hasNext()取决于consumer.timeout.ms的值,默认为-1(阻塞等待),超时会抛出ConsumerTimeoutException异常
                    try {
                        while (iterator.hasNext()) {
                            MessageAndMetadata<byte[], byte[]> messageAndMetadata = iterator.next();
                            String key = new String(messageAndMetadata.key());
                            String message = new String(messageAndMetadata.message());
                            log.info("[kafka]拉取到消息，topic：{}, partition:{}, offset:{}, key:{}, message:{}",
                                    messageAndMetadata.topic(), messageAndMetadata.partition(), messageAndMetadata.offset(), key, message);
                            //处理message
                            if (CollectionUtils.isNotEmpty(iKafkaConsumers)) {
                                threadPoolTaskExecutor.submit(() -> iKafkaConsumers.forEach(consumer -> {
                                    try {
                                        consumer.process(message);
                                    } catch (Exception e) {
                                        log.error("[kafka]处理消息异常,messageAndMetadata:{}", messageAndMetadata, e);
                                    }
                                }));
                            }
                        }
                    } catch (ConsumerTimeoutException e) {
                        log.error("[kafka]消息监听超时,topic:{}", topic, e);
                    }
                }).start();
            });
            log.info("[kafka]消息已被监听,topic:{}", topic);
        });
    }

    @PreDestroy
    public void close() {
        consumerConnector.shutdown();
    }

    private Map<String, List<IKafkaConsumer>> kafkaConsumers() {
        Map<String, IKafkaConsumer> kafkaConsumerMap = applicationContext.getBeansOfType(IKafkaConsumer.class);
        HashMap<String, List<IKafkaConsumer>> topicConsumerMap = new HashMap<>();
        if (MapUtils.isEmpty(kafkaConsumerMap)) {
            return topicConsumerMap;
        }

        kafkaConsumerMap.values().forEach(consumer -> {
            List<IKafkaConsumer> iKafkaConsumers;
            String topic = consumer.topic();
            if (!topicConsumerMap.containsKey(topic)) {
                iKafkaConsumers = new ArrayList<>();
                iKafkaConsumers.add(consumer);
                topicConsumerMap.put(topic, iKafkaConsumers);
            } else {
                iKafkaConsumers = topicConsumerMap.get(topic);
                iKafkaConsumers.add(consumer);
            }
        });
        return topicConsumerMap;
    }

    public void send(String topic, String key, String message) {
        producer.send(new KeyedMessage<>(topic, key, message));
    }

    public void send(String topic, String message) {
        producer.send(new KeyedMessage<>(topic, message));
    }
}
