package com.msdemo.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
public class HomeController {


    @RequestMapping("/")
    public String home() {
        return "<h1>API Gateway for event driven microservices demo.</h1><div>(Were you looking for <a href=\"/api/v1/pancakes\">a working endpoint</a>?)";
    }

}
