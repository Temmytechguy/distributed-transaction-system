package com.temmytechie.ecommerce.deliveryms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.temmytechie.ecommerce.deliveryms.entity.Delivery;
import com.temmytechie.ecommerce.deliveryms.entity.DeliveryRepository;

import com.temmytechie.ecommerce.deliveryms.model.CustomerOrder;
import com.temmytechie.ecommerce.deliveryms.model.DeliveryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Controller;

/**
 * @author TemmyTechie
 */

@Controller
public class DeliveryController {

    @Autowired
    private DeliveryRepository repository;

    @Autowired
    private KafkaTemplate<String, DeliveryEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, DeliveryEvent> kafkaOrderTemplate;

    private final Logger log = LoggerFactory.getLogger(DeliveryController.class);


    @KafkaListener(topics = "new-stock", groupId = "stock-group")
    public void processPayment(String event) throws JsonProcessingException {
        log.info("Ship order for order {}", event);

        Delivery shipment = new Delivery();
        DeliveryEvent inventoryEvent = new ObjectMapper().readValue(event, DeliveryEvent.class);
        CustomerOrder order = inventoryEvent.getOrder();

        try {
            if (order.getAddress() == null) {
                throw new Exception("Address not present");
            }

            shipment.setAddress(order.getAddress());
            shipment.setOrderId(order.getOrderId());

            shipment.setStatus("success");

            repository.save(shipment);
        } catch (Exception e) {
            shipment.setOrderId(order.getOrderId());
            shipment.setStatus("failed");
            repository.save(shipment);

           log.info("order {}", order);

            DeliveryEvent reverseEvent = new DeliveryEvent();
            reverseEvent.setType("STOCK_REVERSED");
            reverseEvent.setOrder(order);
            kafkaTemplate.send("reversed-stock", reverseEvent);
        }
    }
}
