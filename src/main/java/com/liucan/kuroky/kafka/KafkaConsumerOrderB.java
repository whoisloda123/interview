package com.liucan.kuroky.kafka;

import com.liucan.kuroky.kafka.common.AbstractKafkaConsumerOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * B类业务处理订单
 *
 * @author liucan
 * @version 19-5-15
 */
@Slf4j
@Service
public class KafkaConsumerOrderB extends AbstractKafkaConsumerOrder {
    @Override
    public void process(String message) {
        log.info("[B类订单业务]收到订单消息了,message:{}", message);
    }
}
