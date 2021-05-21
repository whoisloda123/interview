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
 *  原理:https://www.cnblogs.com/xifenglou/p/7251112.html
 *      https://blog.csdn.net/qq_29186199/article/details/80827085
 *
 * 一.概念:
 *  a.producer生产者,broker:kafka集群,consumer消费者,topic消息类型,cunsumer-group消费组
 *  b.partition:分片,一个topic包含多个partition
 *  c.leader:多个partiton里面的一个角色,consumer和producer只和leader交互
 *  d.follower:多个partiton里面的多个角色,只负责同步leader数据
 *  e.zookeeper:存储集群的信息
 *
 * 二.producer生产消息
 *  a.写入方式:push 模式将消息发布到 broker,每条消息append到partition中,顺序写磁盘
 *  b.消息路由:发送消息时,根据分区算法找到一个paration,涉及到topic,paration,key,msg
 *      1.指定paration则直接使用
 *      2.未指定 patition 但指定 key，通过对 key 的 value 进行hash 选出一个 patition
 *      3.patition 和 key 都未指定，使用轮询选出一个 patition
 * c.写入流程:
 *      1.producer 从zookeeper中找到该 partition 的 leader
 *      2.消息发送给leader,leader写入本地log
 *      3.followers从leader中pull消息,发送ack
 *      4.leader收到所有followers的ack后,提交commmin,然后返回producer,相当于要所有的followers都有消息才会commit
 * d.消息发送类型
 *      https://www.cnblogs.com/jasongj/p/7912348.html
 *      https://www.jianshu.com/p/5d889a67dcd3
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
 *      在选举新leader时，一个基本的原则是，新的 leader 必须拥有旧 leader commit 过的所有消息
 *
 * 四.consumer 消费消息
 *  a.consumer group
 *      1.一个消息只能被 group 内的一个 consumer 所消费，且 consumer 消费消息时不关注 offset，最后一个 offset 由 zookeeper 保存。
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
 *  七.如何保证消息不丢失不重复
 *  https://blog.csdn.net/weixin_38750084/article/details/82939435
 *  https://msd.misuland.com/pd/2884250068896974728
 *  https://www.e-learn.cn/content/qita/934559
 *      1.消息丢失场景：
 *          同步发送：发出消息后，必须阻塞等待收到通知后，才发送下一条消息，配置ack为1（只保证leader写入成功就commit,如果leader挂了）
 *              解决：所有partition同步后才commit
 *          异步发送：一直往缓冲区写，然后批量发送，如果配置成缓冲池一满，就清空缓冲池里的消息，会丢失消息
 *              解决：不清空缓冲区，阻塞等待
 *      1.也可以，用Exactly once只且一次，消息不丢失不重复，只且消费一次。
 *
 *      1.消费端弄丢了数据 关闭自动提交offset，在自己处理完毕之后手动提交offset，这样就不会丢失数据
 *      2.一般要求设置4个参数来保证消息不丢失
 *       a.设置每个partition有多个follower
 *       b.必须要至少有一个follower同步后，leader才能提交
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
