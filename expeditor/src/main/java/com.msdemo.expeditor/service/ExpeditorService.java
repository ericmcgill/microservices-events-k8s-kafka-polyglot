package com.msdemo.expeditor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msdemo.expeditor.consumer.Receiver;
import com.msdemo.expeditor.model.Metadata;
import com.msdemo.expeditor.model.PancakesResponseModel;
import com.msdemo.expeditor.producer.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpeditorService {
    Logger logger = LoggerFactory.getLogger(ExpeditorService.class);

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        logger.info(">>>>>>> Starting ExpeditorService.");
    }

    public void handleMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            PancakesResponseModel responseModel = mapper.readValue(message, PancakesResponseModel.class);
            Metadata metadata = responseModel.getMetadata();
            List<String> contributors = metadata.getContributors();
            metadata.setCmd("serve pancakes");

            String topic = String.format("order-ready");
            contributors.add("expeditor");
            metadata.setContributors(contributors);
            List<String> data = responseModel.getData();

            data.add("Butter");
            data.add("Syrup");
            data.add("Pancakes");
            responseModel.setData(data);

            String responseModelJson = mapper.writeValueAsString(responseModel);
            sender.send(topic, responseModelJson);

        } catch (Exception e) {
            logger.error(">>>> FAILURE: Things have blown up in handleMessage(): {}", e.getMessage());
        }


    }

}
