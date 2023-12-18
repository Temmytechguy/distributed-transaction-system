package com.temmytechie.ecommerce.stockms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.temmytechie.ecommerce.stockms.repository.StockRepository;

import com.temmytechie.ecommerce.stockms.entity.WareHouse;
import com.temmytechie.ecommerce.stockms.model.CustomerOrder;
import com.temmytechie.ecommerce.stockms.model.DeliveryEvent;
import com.temmytechie.ecommerce.stockms.model.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TemmyTechie
 */

@RestController
@RequestMapping("/api")
public class StockController {

    @Autowired
    private StockRepository repository;

    @Autowired
    private KafkaTemplate<String, DeliveryEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaPaymentTemplate;

    private final Logger log = LoggerFactory.getLogger(StockController.class);


    @KafkaListener(topics = "new-payments", groupId = "payments-group")
    public void updateStock(String paymentEvent) throws JsonProcessingException {
        log.info("Update inventory for order  {}", paymentEvent);

        DeliveryEvent event = new DeliveryEvent();

        PaymentEvent p = new ObjectMapper().readValue(paymentEvent, PaymentEvent.class);
        CustomerOrder order = p.getOrder();

        try {
            Iterable<WareHouse> inventories = repository.findByItem(event.getOrder().getItem());

            boolean exists = inventories.iterator().hasNext();

            if (!exists) {
                log.info("Stock not exist so reverting the order");
                throw new Exception("Stock not available");
            }

            inventories.forEach(i -> {
                i.setQuantity(i.getQuantity() - order.getQuantity());

                repository.save(i);
            });

            event.setType("STOCK_UPDATED");
            event.setOrder(p.getOrder());
            kafkaTemplate.send("new-stock", event);
        } catch (Exception e) {
            PaymentEvent pe = new PaymentEvent();
            pe.setOrder(order);
            pe.setType("PAYMENT_REVERSED");
            kafkaPaymentTemplate.send("reversed-payments", pe);
        }
    }
}
