package com.msdemo.controller;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.*;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

@RestController
@RequestMapping("api/v1/")
public class EventController {

    Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private KafkaAdmin admin;

    @RequestMapping("dynamic-pancakes")
    public String dynamicPancakes() {
        try {
            Future<String> f = sendForWaiting();
            return f.get();
        } catch (Exception e) {
            return "Nope";
        }


    }

    private Future<String> sendForWaiting() {

        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(500);

            return null;
        });

        String uuid = UUID.randomUUID().toString();
        String topic = "api-"+ uuid;

        String brokerAddress = "localhost:9092";

        AdminClient ac = AdminClient.create(admin.getConfig());
        ArrayList<NewTopic> newTopicsList = new ArrayList<>();
        NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
        NewTopic newTopic2 = new NewTopic("incoming", 1, (short) 1);
        newTopicsList.add(newTopic);
        newTopicsList.add(newTopic2);
        final CreateTopicsResult topics = ac.createTopics(newTopicsList);


        try {
            // Gotta block waiting on that thing.
            topics.values().get(topic).get();
            logger.info(">>> Blocking while adding a topic.");
        }
        catch (Exception e) {

        }




        List<String> consumedMessages = new ArrayList<>();
        // Create configuration for your consumer. This is most basic configuration,
        // you probably want to add something more, like 'auto.offset.reset',
        // see: https://kafka.apache.org/documentation/#consumerconfigs
        Map<String, Object> consumerConfig = new HashMap<String, Object>(){
            {
                put(BOOTSTRAP_SERVERS_CONFIG, brokerAddress);
                put(GROUP_ID_CONFIG, "api-consumer");
            }
        };

        // create KafkaConsumerFactory which adds information about parsing key and value
        // you could also do it in config but this is compile-safe approach
        DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerConfig,
                        new StringDeserializer(),
                        new StringDeserializer());

        // you also need container which has info about topic and what to do with messages
        logger.info(String.format("Creating listener on topic: %s", topic));
        ContainerProperties containerProperties = new ContainerProperties(topic);

        final CountDownLatch latch = new CountDownLatch(1);
        containerProperties.setMessageListener((MessageListener<String, String>) message -> {
            latch.countDown();
            logger.info("received: " + message.key() + " " + message.value());
            completableFuture.complete(message.value());

        });

        ConcurrentMessageListenerContainer container =
                new ConcurrentMessageListenerContainer<>(
                        kafkaConsumerFactory,
                        containerProperties);

        // ... which you can finally start()
        logger.info(">>>>> STARTING LISTENER");
        container.start();
        setTimeout(() -> container.stop(), 1000);

        // again, this is minimum required config, you should check
        // https://kafka.apache.org/documentation/#producerconfigs
        Map<String, Object> producerConfig = new HashMap<String, Object>(){
            {
                put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress);
            }
        };

        // create KafkaTemplate from KafkaProducerFactory
        // which specifies serializers
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        producerConfig,
                        new StringSerializer(),
                        new StringSerializer()));
        logger.info(String.format("Sending to topic: %s", topic));
        try
        {
            Thread.sleep(500L);
        } catch (Exception e) {

        }



        //kafkaTemplate.send(topic, String.format("Some random shit: %s", uuid));
        kafkaTemplate.send("incoming", String.format(uuid));
        kafkaTemplate.flush();


        return completableFuture;

    }

    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

}
