package com.msdemo.expeditor.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.msdemo.expeditor.model.PancakesResponseModel;

public class Sender {

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String prm) {
        logger.info("sending pancakes='{}'", prm);
        kafkaTemplate.send(topic, prm);
    }
}