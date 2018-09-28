package com.msdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msdemo.model.Metadata;
import com.msdemo.model.PancakesResponseModel;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.*;
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

    private Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private KafkaAdmin admin;

    @RequestMapping("pancakes")
    public PancakesResponseModel pancakes() {
        try {
            Future<String> f = sendForWaiting();
            ObjectMapper mapper = new ObjectMapper();
            try {
                PancakesResponseModel model = mapper.readValue(f.get(), PancakesResponseModel.class);
                return model;
            } catch (Exception e) {
                return new PancakesResponseModel();
            }

        } catch (Exception e) {
            return new PancakesResponseModel();
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
        NewTopic newTopic2 = new NewTopic("order-in", 1, (short) 1);
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


        // create KafkaTemplate from KafkaProducerFactory
        // which specifies serializers
        KafkaTemplate<String, String> kafkaTemplate = kafkaTemplate();

        logger.info(String.format("Sending to topic: %s", topic));
        try
        {
            Thread.sleep(500L);
        } catch (Exception e) {

        }

        Metadata m = new Metadata();
        m.setId(uuid);
        m.setCmd("order-in");
        List<String> contibutors = new ArrayList<>();
        contibutors.add("waiter (api-gateway)");
        m.setContributors(contibutors);
        m.setRole("waiter");
        m.setTimestamp((int)(System.currentTimeMillis()/1000));
        List<String> data = new ArrayList<>();
        data.add(uuid);

        PancakesResponseModel order = new PancakesResponseModel();
        order.setData(data);
        order.setMetadata(m);

        //kafkaTemplate.send(topic, String.format("Some random shit: %s", uuid));

        ObjectMapper mapper = new ObjectMapper();
        try{
            String orderJson = mapper.writeValueAsString(order);
            kafkaTemplate.send("order-in", orderJson);
            kafkaTemplate.flush();
        } catch (Exception e) {
            logger.error("Oops couldn't convert to JSON>>>>>");
        }

        return completableFuture;

    }

    private static void setTimeout(Runnable runnable, int delay){
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


    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return props;
    }
    private ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }
    private KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
