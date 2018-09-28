package com.msdemo.expeditor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {
    Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String payload) {
        logger.info("Sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        logger.info("Let's start this service up.");
    }

    @KafkaListener(topics = "incoming", groupId = "reandom")
    public void listen(String in) {
        logger.info("Server received: {}", in);
        send(String.format("api-%s", in), String.format("Here you go: %s", in.toUpperCase()));
    }

}