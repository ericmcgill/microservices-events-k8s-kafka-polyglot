//package com.msdemo.expeditor.service;
//
//import com.msdemo.expeditor.model.Metadata;
//import com.msdemo.expeditor.model.PancakesResponseModel;
//import com.msdemo.expeditor.producer.Sender;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class KafkaListenerService {
//    Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);
//
//    @Autowired
//    private Sender sender;
//
//    @EventListener
//    public void onApplicationEvent(ContextRefreshedEvent event)
//    {
//        logger.info("Let's start this service up.");
//    }
//
//    @KafkaListener(topics = "order-in", groupId = "expeditors")
//    public void listen(ConsumerRecord<String, String> record) {
//        String in = record.value();
//        logger.info("Server received: {}", in);
//        String topic = String.format("api-%s", in);
//
//        Metadata m = new Metadata();
//        m.setId(in);
//        m.setCmd("order-up");
//        List<String> contibutors = new ArrayList<>();
//        contibutors.add("waiter");
//        contibutors.add("expeditor");
//        contibutors.add("cook");
//        contibutors.add("cow-milker");
//        contibutors.add("egg-retriever");
//        contibutors.add("syrup-extractor");
//        contibutors.add("wheat-harvester");
//        contibutors.add("miller");
//        contibutors.add("butter-churner");
//        contibutors.add("batter-maker");
//        m.setContributors(contibutors);
//        m.setRole("waiter");
//        m.setTimestamp((int)(System.currentTimeMillis()/1000));
//        List<String> data = new ArrayList<>();
//        data.add(in);
//
//        PancakesResponseModel order = new PancakesResponseModel();
//        order.setData(data);
//        order.setMetadata(m);
//
//        sender.send(topic, order);
//    }
//
//}