//package com.msdemo.consumer;
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Component;
//
////import com.gauravg.model.Model;
//
//
//@Component
//public class ReplyingKafkaConsumer {
//
//    @KafkaListener(topics = "${kafka.topic.requestreply-topic}")
//    public String listen(String request) throws InterruptedException {
//
//        return request;
//    }
//
//}
