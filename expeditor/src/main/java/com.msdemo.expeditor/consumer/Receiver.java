package com.msdemo.expeditor.consumer;

import java.util.concurrent.CountDownLatch;

import com.msdemo.expeditor.service.ExpeditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class Receiver {

    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    @Autowired
    private ExpeditorService handler;

    @KafkaListener(topics = "order-in", groupId = "expeditors", containerFactory = "kafkaListenerContainerFactory")
    public void receive(String message) {
        logger.info("received message='{}'", message);
        handler.handleMessage(message);
        latch.countDown();
    }
}