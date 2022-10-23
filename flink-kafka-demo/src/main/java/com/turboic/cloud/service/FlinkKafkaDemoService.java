package com.turboic.cloud.service;

import com.turboic.cloud.constant.KafkaConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * @author liebe
 */
@Service
public class FlinkKafkaDemoService {
    private static final Logger log = LoggerFactory.getLogger(FlinkKafkaDemoService.class);

    private final KafkaTemplate kafkaTemplate;

    @Autowired
    public FlinkKafkaDemoService(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    /***
     * 异步发送
     * 消息超长，producer没有收到回调信息
     * @param message
     */
    public void product(String message) {
        log.error("kafka product发送消息内容:{}", message);
        ListenableFuture listenableFuture = kafkaTemplate.send(KafkaConstant.default_topic, message);
        listenableFuture.addCallback(
                o -> {
                    log.info("生产者向Kafka Broker 消息发送成功,{}", o.toString());
                },
                throwable -> {
                    log.info("生产者向Kafka Broker 消息发送失败,{}" + throwable.getMessage());
                }
        );
    }



}
